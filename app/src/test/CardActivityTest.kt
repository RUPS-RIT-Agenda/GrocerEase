package com.prvavaja.grocerease

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.lists.CardAdapter
import com.prvavaja.grocerease.model.Card
import com.googlecode.tesseract.android.TessBaseAPI
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class CardActivityTest {

    private lateinit var activity: CardActivity
    private lateinit var tessBaseAPI: TessBaseAPI
    private lateinit var mockAdapter: CardAdapter

    @Before
    fun setUp() {
        // Ustvari primer razreda CardActivity
        activity = CardActivity()
        // Ustvari posmeh za TessBaseAPI
        tessBaseAPI = mock(TessBaseAPI::class.java)
        // Ustvari posmeh za CardAdapter
        mockAdapter = mock(CardAdapter::class.java)

        // Inicializiraj Tesseract OCR posmehovanje
        activity.apply {
            // Override Tesseract API z mockom
            this.tessBaseAPI = tessBaseAPI
        }
    }

    @Test
    fun `extractCodeFromImage should return valid code when OCR recognizes text`() {
        // Pripravi posmehovanje Tesseract API-ja
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("1234567890")

        // Pokliči metodo
        val result = activity.extractCodeFromImage(bitmap)

        // Preveri rezultat
        assertEquals("1234567890", result)
    }

    @Test
    fun `extractCodeFromImage should return null when OCR fails to recognize text`() {
        // Pripravi posmehovanje Tesseract API-ja
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("")

        // Pokliči metodo
        val result = activity.extractCodeFromImage(bitmap)

        // Preveri rezultat
        assertNull(result)
    }

    @Test
    fun `onActivityResult should add card when valid code is extracted`() {
        // Pripravi testne podatke
        val validCode = "1234567890"
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn(validCode)

        // Simuliraj adapter in RecyclerView
        val recyclerView = mock(RecyclerView::class.java)
        `when`(recyclerView.adapter).thenReturn(mockAdapter)
        activity.findViewById<RecyclerView>(R.id.recyclerViewCards).also {
            `when`(it.adapter).thenReturn(mockAdapter)
        }

        // Pokliči metodo
        val card = Card(validCode, "Unknown Shop", null)
        activity.onActivityResult(CardActivity.CAMERA_REQUEST_CODE, AppCompatActivity.RESULT_OK, null)

        // Preveri, ali je bila kartica dodana
        verify(mockAdapter).addCard(card)
    }

    @Test
    fun `onActivityResult should not add card when no valid code is extracted`() {
        // Pripravi testne podatke
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("")

        // Simuliraj adapter in RecyclerView
        val recyclerView = mock(RecyclerView::class.java)
        `when`(recyclerView.adapter).thenReturn(mockAdapter)
        activity.findViewById<RecyclerView>(R.id.recyclerViewCards).also {
            `when`(it.adapter).thenReturn(mockAdapter)
        }

        // Pokliči metodo
        activity.onActivityResult(CardActivity.CAMERA_REQUEST_CODE, AppCompatActivity.RESULT_OK, null)

        // Preveri, ali kartica ni bila dodana
        verify(mockAdapter, never()).addCard(any())
    }
}
