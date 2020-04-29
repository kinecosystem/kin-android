package kin.sdk.internal

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SharedPrefStoreTest {

    private lateinit var sut: SharedPrefStore
    private lateinit var mockSharedPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        mockEditor = mock {}
        whenever(mockEditor.putString(eq("key"), eq("value"))).thenReturn(mockEditor)
        whenever(mockEditor.remove(eq("key"))).thenReturn(mockEditor)
        mockSharedPrefs = mock {
            on { edit() } doReturn mockEditor
            on { getString(eq("key"), eq(null)) } doReturn "value"
        }
        sut = SharedPrefStore(mockSharedPrefs)
    }

    @Test
    fun saveString() {
        sut.saveString("key", "value")

        verify(mockSharedPrefs).edit()
        verify(mockEditor).putString(eq("key"), eq("value"))
    }

    @Test
    fun getString() {

        assertEquals("value", sut.getString("key"))

        verify(mockSharedPrefs).getString(eq("key"), eq(null))
    }

    @Test
    fun clear() {

        sut.clear("key")

        verify(mockSharedPrefs).edit()
        verify(mockEditor).remove(eq("key"))
    }
}
