package com.s2i.inpayment.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.ui.navigation.bottomNavItems

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White, // Gunakan warna dari tema
        modifier = Modifier.fillMaxWidth().padding(8.dp), // Tambahkan padding untuk spacing
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0E526B), // Warna saat dipilih
                    unselectedIconColor = Color(0xFF0E526B), // Warna saat tidak dipilih
                    selectedTextColor = Color(0xFF0E526B), // Warna teks saat dipilih
                    unselectedTextColor = Color(0xFF0E526B) // Warna teks saat tidak dipilih
                )
            )
        }
    }
}


@Preview
@Composable
fun PreviewBottomNavigationBar() {
    var selectedIndex by remember { mutableStateOf(0) }
    BottomNavigationBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it })
}
