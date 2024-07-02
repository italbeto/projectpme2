package com.example.prueba2pme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.prueba2pme.db.AppDatabase
import com.example.prueba2pme.db.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProductActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Crea un boton para volver a la pantalla principal usando Scaffold y Compose
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { finish() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            ) {
                // Contenido principal centrado en una caja
                Box(modifier = Modifier.fillMaxSize()) {
                    AddProductUI(
                        onSave = { productName ->
                            saveProduct(productName) // Llama a la función para guardar el producto
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Función para guardar un nuevo producto en la base de datos
    private fun saveProduct(productName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(this@AddProductActivity).productoDao()
            dao.insertar(Producto(id = 0, producto = productName, realizada = false))

            // Establece el resultado y finaliza la actividad en el hilo principal
            withContext(Dispatchers.Main) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}

@Composable
fun AddProductUI(onSave: (String) -> Unit, modifier: Modifier = Modifier) {
    val (productName, setProductName) = remember { mutableStateOf("") }

    // Diseño de la interfaz de usuario para agregar un producto
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Icono de carrito de compras
        Icon(
            Icons.Filled.ShoppingCart,
            contentDescription = "Icono de carrito de compras",
            tint = Color.Gray,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Campo de texto para ingresar el nombre del producto
        OutlinedTextField(
            value = productName,
            onValueChange = setProductName,
            label = { Text(stringResource(id = R.string.txtIngresarProducto)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón para guardar el producto
        Button(
            onClick = { onSave(productName) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.btnguardar))
        }
    }
}
