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
        activity = CardActivity()
        tessBaseAPI = mock(TessBaseAPI::class.java)
        mockAdapter = mock(CardAdapter::class.java)

        activity.apply {
            this.tessBaseAPI = tessBaseAPI
        }
    }

    @Test
    fun `extractCodeFromImage should return valid code when OCR recognizes text`() {
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("1234567890")

        val result = activity.extractCodeFromImage(bitmap)

        assertEquals("1234567890", result)
    }

    @Test
    fun `extractCodeFromImage should return null when OCR fails to recognize text`() {
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("")

        val result = activity.extractCodeFromImage(bitmap)

        assertNull(result)
    }

    @Test
    fun `onActivityResult should add card when valid code is extracted`() {
        val validCode = "1234567890"
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn(validCode)

        val recyclerView = mock(RecyclerView::class.java)
        `when`(recyclerView.adapter).thenReturn(mockAdapter)
        activity.findViewById<RecyclerView>(R.id.recyclerViewCards).also {
            `when`(it.adapter).thenReturn(mockAdapter)
        }

        val card = Card(validCode, "Unknown Shop", null)
        activity.onActivityResult(CardActivity.CAMERA_REQUEST_CODE, AppCompatActivity.RESULT_OK, null)

        verify(mockAdapter).addCard(card)
    }

    @Test
    fun `onActivityResult should not add card when no valid code is extracted`() {
        val bitmap = mock(Bitmap::class.java)
        `when`(tessBaseAPI.utF8Text).thenReturn("")

        val recyclerView = mock(RecyclerView::class.java)
        `when`(recyclerView.adapter).thenReturn(mockAdapter)
        activity.findViewById<RecyclerView>(R.id.recyclerViewCards).also {
            `when`(it.adapter).thenReturn(mockAdapter)
        }

        activity.onActivityResult(CardActivity.CAMERA_REQUEST_CODE, AppCompatActivity.RESULT_OK, null)

        verify(mockAdapter, never()).addCard(any())
    }
}
