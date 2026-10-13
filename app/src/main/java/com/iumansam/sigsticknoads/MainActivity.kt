package com.iumansam.sigsticknoads

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.net.URLEncoder

class MainActivity : Activity() {
    private val bg = Color.rgb(12, 12, 19)
    private val surface = Color.rgb(25, 25, 37)
    private val purple = Color.rgb(167, 139, 250)
    private val white = Color.rgb(247, 245, 255)
    private val muted = Color.rgb(174, 171, 196)

    private lateinit var root: LinearLayout
    private lateinit var scroll: ScrollView
    private lateinit var content: LinearLayout
    private lateinit var search: EditText
    private lateinit var prefs: SharedPreferences
    private lateinit var catalogSection: LinearLayout

    private data class Catalog(
        val name: String,
        val description: String,
        val emoji: String,
        val url: String,
        val tag: String
    )

    private val catalogs = listOf(
        Catalog(
            "SigStick",
            "Comunidad de stickers, memes y packs para chats.",
            "✨",
            "https://www.sigstick.com/",
            "COMUNIDAD"
        ),
        Catalog(
            "Stickers Cloud",
            "Colecciones y paquetes para distintas plataformas.",
            "☁️",
            "https://stickers.cloud/es/",
            "MULTIPLATAFORMA"
        ),
        Catalog(
            "Stickers.wiki",
            "Catálogo de WhatsApp organizado por temas.",
            "🎭",
            "https://stickers.wiki/es/whatsapp/catalog/",
            "WHATSAPP"
        ),
        Catalog(
            "Combot Stickers",
            "Busca colecciones populares y tendencias de Telegram.",
            "🚀",
            "https://combot.org/stickers",
            "TENDENCIAS"
        ),
        Catalog(
            "Telegram Stickers",
            "Directorio de colecciones populares y animadas.",
            "🎞️",
            "https://tlgrm.eu/stickers",
            "DIRECTORIO"
        ),
        Catalog(
            "WhatsApp oficial",
            "Colecciones oficiales disponibles para WhatsApp.",
            "💬",
            "https://www.whatsapp.com/business-stickers?lang=es",
            "OFICIAL"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("sigstick_noads", MODE_PRIVATE)

        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0

        buildInterface()
    }

    private fun buildInterface() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(15), dp(20), dp(12))
        }

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        brand.addView(label("STICKER DISCOVERY", 10, purple, true))
        brand.addView(label("SigStick NoAds", 23, white, true))

        val badge = label("SIN ANUNCIOS", 10, purple, true)
        badge.background = rounded(surface, 18)
        badge.setPadding(dp(12), dp(8), dp(12), dp(8))

        header.addView(brand)
        header.addView(badge)
        root.addView(header)

        scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(5), dp(18), dp(25))
        }

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        buildHero()
        buildSearch()
        buildQuickSearches()
        buildCatalogs()
        buildViralSection()
        buildFooter()
        buildBottomBar()

        setContentView(root)
    }

    private fun buildHero() {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(
                Color.rgb(42, 29, 77), 24
            )
            setPadding(dp(20), dp(22), dp(20), dp(22))
        }

        card.addView(label("TU UNIVERSO DE STICKERS", 11, purple, true))
        card.addView(space(9))
        card.addView(label("Encuentra tu próxima reacción.", 27, white, true))
        card.addView(space(8))
        card.addView(
            label(
                "Explora catálogos, descubre memes y encuentra colecciones para tus chats.",
                14, muted, false
            )
        )
        card.addView(space(16))

        val features = label(
            "✦  Memes     ✦  Reacciones     ✦  Tendencias",
            12, white, true
        )
        card.addView(features)
        content.addView(card)
        content.addView(space(20))
    }

    private fun buildSearch() {
        content.addView(label("BUSCADOR GLOBAL", 12, muted, true))
        content.addView(space(9))

        search = EditText(this).apply {
            hint = "Ej. memes TikTok, risa, RD..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 15f
            singleLine = true
            setPadding(dp(15), dp(12), dp(15), dp(12))
            background = rounded(surface, 15)
            imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
            setOnEditorActionListener { _, _, _ ->
                doSearch(search.text.toString())
                true
            }
        }
        content.addView(
            search,
            LinearLayout.LayoutParams(-1, dp(54))
        )
        content.addView(space(9))

        val searchButton = button("⌕   Buscar en los catálogos", purple, bg)
        searchButton.setOnClickListener {
            doSearch(search.text.toString())
        }
        content.addView(searchButton)
        content.addView(space(21))
    }

    private fun buildQuickSearches() {
        content.addView(label("EXPLORA POR TEMA", 12, muted, true))
        content.addView(space(10))

        val row = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
        }
        val chips = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val topics = listOf(
            "🔥 TikTok viral",
            "😂 Memes",
            "🇩🇴 RD",
            "😭 Reacciones",
            "🐱 Gatos",
            "🎌 Anime",
            "💀 Humor negro"
        )

        topics.forEach { topic ->
            val chip = button(topic, surface, white)
            val params = LinearLayout.LayoutParams(-2, dp(43))
            params.marginEnd = dp(8)
            chips.addView(chip, params)
            chip.setOnClickListener { doSearch(topic) }
            chip.setOnLongClickListener {
                saveFavorite(topic, null)
                true
            }
        }

        row.addView(chips)
        content.addView(row)
        content.addView(
            label("Mantén pulsado un tema para guardarlo.", 11, muted, false)
        )
        content.addView(space(24))
    }

    private fun buildCatalogs() {
        catalogSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        catalogSection.addView(label("CATÁLOGOS DE LA COMUNIDAD", 12, muted, true))
        catalogSection.addView(space(6))
        catalogSection.addView(
            label("Más fuentes, más posibilidades.", 21, white, true)
        )
        catalogSection.addView(space(13))

        catalogs.forEach { catalog ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(surface, 18)
                setPadding(dp(15), dp(15), dp(15), dp(15))
                isClickable = true
                isFocusable = true
            }

            val emoji = label(catalog.emoji, 27, white, false)
            emoji.gravity = Gravity.CENTER
            emoji.background = rounded(
                Color.rgb(41, 37, 59), 15
            )
            card.addView(
                emoji,
                LinearLayout.LayoutParams(dp(54), dp(54))
            )

            val info = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, dp(5), 0)
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            }

            info.addView(label(catalog.name, 16, white, true))
            info.addView(space(4))
            info.addView(label(catalog.description, 12, muted, false))
            info.addView(space(6))
            info.addView(label(catalog.tag, 9, purple, true))

            card.addView(info)
            card.addView(label("›", 28, purple, true))

            card.setOnClickListener {
                openCatalog(catalog.name, catalog.url)
            }
            card.setOnLongClickListener {
                saveFavorite(catalog.name, catalog.url)
                true
            }

            catalogSection.addView(card)
            catalogSection.addView(space(10))
        }

        content.addView(catalogSection)
        content.addView(space(18))
    }

    private fun buildViralSection() {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.rgb(30, 25, 46), 20)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        card.addView(label("⚡ ZONA VIRAL", 11, purple, true))
        card.addView(space(7))
        card.addView(label("¿Qué está pegado en TikTok?", 20, white, true))
        card.addView(space(7))
        card.addView(
            label(
                "Busca memes, audios convertidos en imágenes, reacciones y frases virales en varios catálogos.",
                13, muted, false
            )
        )
        card.addView(space(13))

        val topics = listOf(
            "Memes virales TikTok",
            "Reacciones de streamers",
            "Memes dominicanos",
            "Frases virales"
        )

        topics.forEach { topic ->
            val item = button("↗   $topic", surface, white)
            item.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            card.addView(item)
            card.addView(space(7))
            item.setOnClickListener { doSearch(topic) }
        }

        content.addView(card)
        content.addView(space(20))
    }

    private fun buildFooter() {
        val footer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(16), dp(12), dp(10))
        }
        footer.addView(label("HECHO PARA DESCUBRIR", 10, purple, true))
        footer.addView(space(6))
        footer.addView(
            label(
                "Sin SDK publicitario · Sin cuenta propia · Sin servidor local",
                11, muted, false
            )
        )
        footer.addView(space(6))
        footer.addView(
            label(
                "Los catálogos externos conservan sus propias condiciones.",
                10, muted, false
            )
        )
        content.addView(footer)
    }

    private fun buildBottomBar() {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(6), dp(7), dp(6), dp(7))
            setBackgroundColor(Color.rgb(18, 18, 27))
        }

        val home = navButton("⌂", "Inicio")
        val catalogsButton = navButton("▦", "Catálogos")
        val viral = navButton("✦", "Viral")
        val saved = navButton("♡", "Guardados")

        listOf(home, catalogsButton, viral, saved).forEach {
            bar.addView(
                it,
                LinearLayout.LayoutParams(0, dp(54), 1f)
            )
        }

        home.setOnClickListener { scroll.smoothScrollTo(0, 0) }
        catalogsButton.setOnClickListener {
            scroll.post {
                scroll.smoothScrollTo(0, catalogSection.top)
            }
        }
        viral.setOnClickListener { doSearch("TikTok viral memes") }
        saved.setOnClickListener { showFavorites() }

        root.addView(bar)
    }

    private fun navButton(icon: String, name: String): LinearLayout {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        box.addView(label(icon, 22, purple, true))
        box.addView(label(name, 10, white, false))
        box.isClickable = true
        box.isFocusable = true
        return box
    }

    private fun doSearch(raw: String) {
        val query = raw.trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Escribe qué sticker buscas.", Toast.LENGTH_SHORT).show()
            return
        }

        val fullQuery =
            "WhatsApp sticker pack $query " +
            "(site:sigstick.com OR site:stickers.cloud OR site:stickers.wiki)"

        val encoded = URLEncoder.encode(fullQuery, "UTF-8")
        openCatalog(
            "Resultados: $query",
            "https://www.google.com/search?q=$encoded"
        )
    }

    private fun openCatalog(title: String, url: String) {
        startActivity(
            Intent(this, CatalogActivity::class.java).apply {
                putExtra("title", title)
                putExtra("url", url)
            }
        )
    }

    private fun saveFavorite(title: String, url: String?) {
        val items = prefs.getStringSet("favorites", emptySet())
            ?.toMutableSet() ?: mutableSetOf()

        val entry = "$title|||${url.orEmpty()}"

        if (items.contains(entry)) {
            items.remove(entry)
            Toast.makeText(this, "Eliminado de guardados", Toast.LENGTH_SHORT).show()
        } else {
            items.add(entry)
            Toast.makeText(this, "Guardado: $title", Toast.LENGTH_SHORT).show()
        }

        prefs.edit().putStringSet("favorites", items).apply()
    }

    private fun showFavorites() {
        val items = prefs.getStringSet("favorites", emptySet())
            ?.toList()?.sorted() ?: emptyList()

        if (items.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Tus guardados")
                .setMessage("Mantén pulsado un tema o catálogo para guardarlo aquí.")
                .setPositiveButton("Entendido", null)
                .show()
            return
        }

        val names = items.map { it.substringBefore("|||") }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Tus guardados")
            .setItems(names) { _, which ->
                val parts = items[which].split("|||", limit = 2)
                val title = parts[0]
                val url = parts.getOrElse(1) { "" }

                if (url.isNotBlank()) {
                    openCatalog(title, url)
                } else {
                    doSearch(title)
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun label(
        text: String,
        size: Int,
        color: Int,
        bold: Boolean
    ): TextView = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER_VERTICAL
        includeFontPadding = true
    }

    private fun button(
        text: String,
        color: Int,
        textColor: Int
    ): Button = Button(this).apply {
        this.text = text
        textSize = 13f
        setTextColor(textColor)
        isAllCaps = false
        background = rounded(color, 14)
        minHeight = dp(43)
        minimumHeight = dp(43)
        setPadding(dp(12), dp(5), dp(12), dp(5))
        stateListAnimator = null
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun space(height: Int): View =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(height))
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
