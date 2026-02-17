package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eventcoord.R

@Composable
fun ProfileScreen(onBackClick: () -> Unit){
    val usuario = painterResource(R.drawable.usuario)
    Scaffold(modifier = Modifier.fillMaxSize(),
            bottomBar= {
                Card( // crea la barra con los text button para la navegacion del usuario
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {

                        TextButton(
                            onClick = {}
                        ) {
                            Text("EDITAR")

                        }
                        Spacer(modifier= Modifier.width(5.dp))
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.White
                            )

                        }
                    }
                }
            }


    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize() //
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("PERFIL")
            Spacer(modifier = Modifier.height(7.dp))
            Image(
                painter = usuario,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(160.dp)
                    .height(160.dp)
                    .padding(8.dp)
            )
        }
    }
}