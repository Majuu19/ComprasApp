package com.example.bruno.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bruno.model.Item
import com.example.bruno.model.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ShoppingRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    private val TAG = "ShoppingRepository"


    private val _lists = MutableLiveData<List<ShoppingList>>(emptyList())
    val lists: LiveData<List<ShoppingList>> get() = _lists
    private var listsListener: ListenerRegistration? = null
    private var currentListQuery: String = ""


    private val _items = MutableLiveData<List<Item>>(emptyList())
    val items: LiveData<List<Item>> get() = _items
    private var itemsListener: ListenerRegistration? = null
    private var currentItemQuery: String = ""
    private var currentListId: String = ""

    fun setListQuery(query: String) {
        this.currentListQuery = query.lowercase()
        startObservingLists()
    }


    fun startObservingLists() {
        val user = auth.currentUser ?: return
        listsListener?.remove()

        var firestoreQuery: Query = db.collection("shopping_lists")
            .whereEqualTo("userId", user.uid)
            .orderBy("titleLowercase")

        if (currentListQuery.isNotEmpty()) {
            firestoreQuery = firestoreQuery.startAt(currentListQuery)
                .endAt(currentListQuery + '\uf8ff')
        }

        listsListener = firestoreQuery.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erro ao ouvir as listas do Firestore.", error)
                    return@addSnapshotListener
                }
                val listResult = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ShoppingList>()?.copy(id = doc.id)
                } ?: emptyList()
                _lists.value = listResult
            }
    }

    fun clearListsListener() {
        listsListener?.remove()
        listsListener = null
    }


    suspend fun addList(title: String, imageUri: Uri?) {
        try {
            val user = auth.currentUser ?: return
            val docRef = db.collection("shopping_lists").document()
            var finalImageUrl: String? = null
            if (imageUri != null) {
                finalImageUrl = uploadListImage(user.uid, docRef.id, imageUri)
            }
            val newList = ShoppingList(
                id = docRef.id,
                title = title,
                titleLowercase = title.lowercase(),
                userId = user.uid,
                imageUrl = finalImageUrl
            )
            docRef.set(newList).await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO ADICIONAR LISTA", e)
        }
    }


    suspend fun updateList(list: ShoppingList, newTitle: String, imageUri: Uri?) {
        try {
            val user = auth.currentUser ?: return
            var finalImageUrl = list.imageUrl
            if (imageUri != null) {
                finalImageUrl = uploadListImage(user.uid, list.id, imageUri)
            }
            val updatedList = list.copy(
                title = newTitle,
                titleLowercase = newTitle.lowercase(),
                imageUrl = finalImageUrl
            )
            db.collection("shopping_lists")
                .document(list.id)
                .set(updatedList)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO ATUALIZAR LISTA", e)
        }
    }


    suspend fun deleteList(list: ShoppingList) {
        try {
            val docRef = db.collection("shopping_lists").document(list.id)
            val itemsSnap = docRef.collection("items").get().await()
            for (itemDoc in itemsSnap.documents) {
                itemDoc.reference.delete().await()
            }
            list.imageUrl?.let {
                storage.getReferenceFromUrl(it).delete().await()
            }
            docRef.delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO DELETAR LISTA", e)
        }
    }


    fun setItemQuery(query: String) {
        this.currentItemQuery = query.lowercase()
        startObservingItems(this.currentListId)
    }

    fun startObservingItems(listId: String) {
        this.currentListId = listId
        itemsListener?.remove()

        var itemsQuery: Query = db.collection("shopping_lists")
            .document(listId)
            .collection("items")
            .orderBy("nameLowercase")

        if (currentItemQuery.isNotEmpty()) {
            itemsQuery = itemsQuery.startAt(currentItemQuery)
                .endAt(currentItemQuery + '\uf8ff')
        }

        itemsListener = itemsQuery.addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val itemsResult = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Item>()?.copy(id = doc.id)
                } ?: emptyList()
                _items.value = itemsResult
            }
    }

    fun stopObservingItems() {
        itemsListener?.remove()
        itemsListener = null
    }

    suspend fun addItem(listId: String, item: Item) {
        try {
            val itemWithLowercase = item.copy(nameLowercase = item.name.lowercase())
            db.collection("shopping_lists")
                .document(listId)
                .collection("items")
                .add(itemWithLowercase)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO ADICIONAR ITEM", e)
        }
    }

    suspend fun updateItem(listId: String, item: Item) {
        try {
            val itemWithLowercase = item.copy(nameLowercase = item.name.lowercase())
            db.collection("shopping_lists")
                .document(listId)
                .collection("items")
                .document(item.id)
                .set(itemWithLowercase)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO ATUALIZAR ITEM", e)
        }
    }

    suspend fun toggleBought(listId: String, item: Item) {
        try {
            db.collection("shopping_lists")
                .document(listId)
                .collection("items")
                .document(item.id)
                .update("bought", !item.bought)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO MARCAR/DESMARCAR ITEM", e)
        }
    }

    suspend fun deleteItem(listId: String, itemId: String) {
        try {
            db.collection("shopping_lists")
                .document(listId)
                .collection("items")
                .document(itemId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "ERRO AO DELETAR ITEM", e)
        }
    }

    private suspend fun uploadListImage(
        userId: String,
        listId: String,
        imageUri: Uri
    ): String {
        val ref = storage.reference
            .child("lists")
            .child(userId)
            .child("$listId.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}