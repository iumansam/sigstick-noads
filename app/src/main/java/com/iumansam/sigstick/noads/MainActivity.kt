package com.iumansam.sigstick.noads

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFF9D5CFF)
private val Bg = Color(0xFF0E0A14)
private val Card2 = Color(0xFF221A30)
private val Txt = Color(0xFFF2EBFF)
private val Dim = Color(0xFFA99BC4)

private class Cat(val name: String, val url: String, val dom: String)

private val CATALOGS = listOf(
    Cat("SigStick", "https://www.sigstick.com/", "sigstick.com"),
    Cat("Stickers.cloud", "https://stickers.cloud/es/", "stickers.cloud"),
    Cat("Stickers.wiki", "https://stickers.wiki/es/whatsapp/catalog/", "stickers.wiki"),
    Cat("Combot", "https://combot.org/stickers", "combot.org"),
    Cat("TLGRM", "https://tlgrm.eu/stickers", "tlgrm.eu"),
    Cat("WhatsApp Business", "https://www.whatsapp.com/business-stickers?lang=es", "whatsapp.com")
)

private val TOPICS = mapOf(
    "Memes virales" to listOf("Memes virales", "Sticker gracioso", "Cara de WTF", "Aplausos", "Cringe"),
    "Reacciones" to listOf("Jajaja", "Gracias", "Sorpresa", "Enojado", "Triste", "Llorando de risa"),
    "Animales" to listOf("Gatos", "Perros", "Capibara", "Pato", "Mapache"),
    "Anime" to listOf("Anime", "Chibi", "Kawaii"),
    "F\u00fatbol" to listOf("F\u00fatbol", "Gol", "Mundial"),
    "Amor y amistad" to listOf("Te quiero", "Buenos d\u00edas", "Buenas noches", "Abrazo", "Feliz cumplea\u00f1os"),
    "Gaming" to listOf("Gamer", "Videojuegos", "Minecraft"),
    "Frases" to listOf("Frases divertidas", "Frases motivadoras")
)

private fun find(c: Cat, t: String): String =
    "https://www.google.com/search?q=" + Uri.encode("site:" + c.dom + " " + t + " stickers")

private fun go(c: Context, u: String) {
    try {
        c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        Toast.makeText(c, "Sin navegador", Toast.LENGTH_SHORT).show()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Purple, onPrimary = Color.White,
                    background = Bg, onBackground = Txt,
                    surface = Color(0xFF181220), onSurface = Txt,
                    secondaryContainer = Color(0xFF3B2A66), onSecondaryContainer = Txt
                )
            ) { App() }
        }
    }
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Card2),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        content = content
    )
}

@Composable
private fun Screen(title: String, sub: String, body: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column {
                Text(title, color = Txt, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(sub, color = Dim, fontSize = 14.sp)
            }
        }
        body()
    }
}

@Composable
private fun CatCard(c: Cat, label: String, onClick: () -> Unit) {
    Panel {
        Column(Modifier.padding(16.dp)) {
            Text(c.name, color = Txt, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White)
            ) { Text(label) }
        }
    }
}

@Composable
private fun Term(t: String, fav: Boolean, tog: (String) -> Unit, pick: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { pick(t) }.padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(t, color = Txt, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(
            if (fav) "\u2665" else "\u2661",
            color = Purple,
            fontSize = 24.sp,
            modifier = Modifier.clickable { tog(t) }.padding(14.dp)
        )
    }
}

@Composable
private fun Search(favs: Set<String>, tog: (String) -> Unit, pick: (String) -> Unit) {
    val ctx = LocalContext.current
    var q by remember { mutableStateOf("") }
    val s = q.trim()
    Screen("SigStick NoAds", "Busca stickers en 6 cat\u00e1logos, sin anuncios en la app") {
        item {
            OutlinedTextField(
                value = q,
                onValueChange = { q = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("gatos, jajaja, buenos d\u00edas...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple, unfocusedBorderColor = Card2, cursorColor = Purple,
                    focusedTextColor = Txt, unfocusedTextColor = Txt,
                    focusedContainerColor = Card2, unfocusedContainerColor = Card2
                )
            )
        }
        if (s.isEmpty()) {
            item {
                Text(
                    "Escribe un tema o elige una categor\u00eda. La b\u00fasqueda se abre en el navegador, dentro de cada cat\u00e1logo.",
                    color = Dim, fontSize = 13.sp
                )
            }
        } else {
            item {
                OutlinedButton(onClick = { tog(s) }) {
                    Text(if (favs.contains(s)) "\u2665 Guardado" else "\u2661 Guardar en favoritos", color = Purple)
                }
            }
            items(CATALOGS) { c -> CatCard(c, "Buscar en " + c.name) { go(ctx, find(c, s)) } }
            val m = TOPICS.values.flatten().filter { it.contains(s, ignoreCase = true) }.distinct()
            items(m) { t -> Panel { Term(t, favs.contains(t), tog, pick) } }
        }
    }
}

@Composable
private fun Cats(favs: Set<String>, tog: (String) -> Unit, pick: (String) -> Unit) {
    var ex by remember { mutableStateOf("") }
    Screen("Categor\u00edas", "Memes y temas virales en espa\u00f1ol") {
        items(TOPICS.keys.toList()) { n ->
            Panel {
                Row(
                    Modifier.fillMaxWidth().clickable { ex = if (ex == n) "" else n }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(n, color = Txt, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(if (ex == n) "\u25b2" else "\u25bc", color = Purple)
                }
                if (ex == n) {
                    TOPICS.getValue(n).forEach { t -> Term(t, favs.contains(t), tog, pick) }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun Catalogs() {
    val ctx = LocalContext.current
    Screen("Cat\u00e1logos", "Se abren en tu navegador") {
        items(CATALOGS) { c -> CatCard(c, "Abrir cat\u00e1logo") { go(ctx, c.url) } }
    }
}

@Composable
private fun Favs(favs: Set<String>, tog: (String) -> Unit, pick: (String) -> Unit) {
    Screen("Favoritos", "Guardados solo en este tel\u00e9fono") {
        if (favs.isEmpty()) {
            item { Text("A\u00fan no tienes favoritos. Toca \u2661 junto a un tema.", color = Dim) }
        } else {
            items(favs.sorted()) { t -> Panel { Term(t, true, tog, pick) } }
        }
    }
}

@Composable
private fun App() {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("favoritos", Context.MODE_PRIVATE) }
    var favs by remember { mutableStateOf(prefs.getStringSet("items", emptySet())?.toSet() ?: emptySet()) }
    var tab by remember { mutableIntStateOf(0) }
    var pick by remember { mutableStateOf<String?>(null) }
    val tog: (String) -> Unit = { t ->
        favs = if (favs.contains(t)) favs - t else favs + t
        prefs.edit().putStringSet("items", HashSet(favs)).apply()
    }
    val pk: (String) -> Unit = { t -> pick = t }
    val names = listOf("Buscar", "Categor\u00edas", "Cat\u00e1logos", "Favoritos")

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF181220)) {
                names.forEachIndexed { i, n ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text(if (i == 3) "\u2665" else "\u25cf", color = Purple) },
                        label = { Text(n, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (tab) {
                0 -> Search(favs, tog, pk)
                1 -> Cats(favs, tog, pk)
                2 -> Catalogs()
                else -> Favs(favs, tog, pk)
            }
        }
    }

    pick?.let { t ->
        AlertDialog(
            onDismissRequest = { pick = null },
            containerColor = Card2,
            title = { Text("Buscar en...", color = Txt) },
            text = {
                Column {
                    CATALOGS.forEach { c ->
                        TextButton(onClick = { go(ctx, find(c, t)); pick = null }) { Text(c.name, color = Purple) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { pick = null }) { Text("Cerrar", color = Dim) } }
        )
    }
}
// FIN-SIGSTICK
