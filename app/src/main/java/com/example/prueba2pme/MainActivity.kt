package com.example.prueba2pme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prueba2pme.db.AppDatabase
import com.example.prueba2pme.db.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_CODE_ADD_PRODUCT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen() // Define la pantalla principal usando Compose
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == RESULT_OK) {
            setContent {
                MainScreen() // Vuelve a cargar la pantalla principal al agregar un producto
            }
        }
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val (productos, setProductos) = remember { mutableStateOf(emptyList<Producto>()) }
        val listaVaciaMsj = stringResource(id = R.string.txtNoProductos)

        // Carga inicial de productos desde la base de datos en un efecto lanzado
        LaunchedEffect(productos) {
            withContext(Dispatchers.IO) {
                val dao = AppDatabase.getInstance(context).productoDao()
                setProductos(dao.findAll())
            }
        }

        // Diseña la pantalla usando Scaffold de Material3 con un FloatingActionButton
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val intent = Intent(context, AddProductActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Producto")
                }
            }
        ) { paddingValues ->
            // Muestra un mensaje si no hay productos, o muestra la lista de productos usando LazyColumn
            if (productos.isEmpty()) {
                Text(
                    text = listaVaciaMsj,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(vertical = 20.dp)
                        .padding(horizontal = 16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productos) { producto ->
                        ProductoItemUI(producto) {
                            setProductos(emptyList<Producto>()) // Actualiza la lista de productos
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProductoItemUI(producto: Producto, onSave: () -> Unit = {}) {
        val contexto = LocalContext.current
        val alcanceCorrutina = rememberCoroutineScope()

        // Define un elemento de producto en una fila usando Compose
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            // Muestra un icono de verificación si el producto está realizado, o un carrito de compras si no lo está
            if (producto.realizada) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Producto realizado",
                    tint = Color.Green,
                    modifier = Modifier.clickable {
                        alcanceCorrutina.launch(Dispatchers.IO) {
                            val dao = AppDatabase.getInstance(contexto).productoDao()
                            producto.realizada = false
                            dao.actualizar(producto) // Actualiza el estado del producto en la base de datos
                            onSave() // Ejecuta la función de callback
                        }
                    }
                )
            } else {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Producto faltante",
                    modifier = Modifier.clickable {
                        alcanceCorrutina.launch(Dispatchers.IO) {
                            val dao = AppDatabase.getInstance(contexto).productoDao()
                            producto.realizada = true
                            dao.actualizar(producto) // Actualiza el estado del producto en la base de datos
                            onSave() // Ejecuta la función de callback
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.width(20.dp))

            // Muestra el nombre del producto
            Text(
                text = producto.producto,
                modifier = Modifier.weight(2f)
            )

            // Muestra un icono de eliminar producto con funcionalidad de eliminación
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Eliminar Producto",
                tint = Color.Red,
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        dao.eliminar(producto) // Elimina el producto de la base de datos
                        onSave() // Ejecuta la función de callback
                    }
                }
            )
        }
    }
}
