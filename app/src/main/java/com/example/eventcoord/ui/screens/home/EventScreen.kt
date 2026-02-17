package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EventScreen (onBackClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar= {
            Card( // crea la barra con los text button para la navegacion del usuario
                modifier = Modifier
                    .fillMaxWidth(),
                //horizontalAlignment = Alignment.BottomEnd,
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray
                )
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Spacer(modifier= Modifier.width(5.dp))
                    TextButton(
                        onClick = {}
                    ) {
                        Text("EDITAR")

                    }
                    Spacer(modifier= Modifier.width(5.dp))

                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                //.align(Alignment.End)
                                .padding(5.dp)
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
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize() //
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           Row(
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   text="NO TIENES EVENTOS RECIENTES",
                   textAlign = TextAlign.Center,
                   modifier = Modifier
               )
           }

        }

    }
}