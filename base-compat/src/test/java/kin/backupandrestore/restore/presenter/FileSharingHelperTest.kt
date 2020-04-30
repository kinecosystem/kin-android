package kin.backupandrestore.restore.presenter

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.EXTRA_INTENT
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.restore.presenter.FileSharingHelper.INTENT_TYPE_ALL_IMAGE
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_CODE_IMAGE
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_CANCELED
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_FAILED
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_OK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FileSharingHelperTest {
    companion object {
        const val SDK_INT = "SDK_INT"
    }

    private val fragment: Fragment = mock()
    private lateinit var fileSharingHelper: FileSharingHelper

    private val intentCaptor = argumentCaptor<Intent>()

    @Before
    fun setUp() {
        fileSharingHelper = FileSharingHelper(fragment)
    }

//    @Test
//    fun `request image file with title, android os above and equal to KITKAT`() {
//        val title = "incredible title"
//        setFinalStatic(Build.VERSION::class.java.getField(SDK_INT), 19)
//        fileSharingHelper.requestImageFile(title)
//        verify(fragment).startActivityForResult(intentCaptor.capture(), any())
//        val intent = intentCaptor.firstValue
//        intent.apply {
//            assertEquals(Intent.ACTION_CHOOSER, action)
//            val targetIntent = getParcelableExtra<Intent>(EXTRA_INTENT)
//            targetIntent.apply {
//                assertEquals(Intent.ACTION_OPEN_DOCUMENT, action)
//                assertEquals(1, categories.size)
//                assertEquals(Intent.CATEGORY_OPENABLE, categories.elementAt(0))
//                assertEquals(INTENT_TYPE_ALL_IMAGE, type)
//            }
//        }
//    }

//    @Test
//    fun `request image file with title, android os below KITKAT`() {
//        val title = "incredible title"
//        setFinalStatic(Build.VERSION::class.java.getField(SDK_INT), 18)
//        fileSharingHelper.requestImageFile(title)
//        verify(fragment).startActivityForResult(intentCaptor.capture(), any())
//        val intent = intentCaptor.firstValue
//        intent.apply {
//            assertEquals(Intent.ACTION_CHOOSER, action)
//            val targetIntent = getParcelableExtra<Intent>(EXTRA_INTENT)
//            targetIntent.apply {
//                assertEquals(Intent.ACTION_PICK, action)
//                assertNull(categories)
//                assertEquals(INTENT_TYPE_ALL_IMAGE, type)
//            }
//        }
//    }

    @Test
    fun `extract uri from result, request code is not REQUEST_CODE_IMAGE should failed`() {
        val requestFileResult = fileSharingHelper.extractUriFromActivityResult(564, 65, null)
        requestFileResult.apply {
            assertEquals(REQUEST_RESULT_FAILED, result)
            assertNull(fileUri)
        }
    }

    @Test
    fun `extract uri from result, result code is RESULT_CANCELED return cancel `() {
        val requestFileResult = fileSharingHelper.extractUriFromActivityResult(
            REQUEST_CODE_IMAGE,
            RESULT_CANCELED,
            null
        )
        requestFileResult.apply {
            assertEquals(REQUEST_RESULT_CANCELED, result)
            assertNull(fileUri)
        }
    }

    @Test
    fun `extract uri from result, result code is RESULT_OK return ok with correct data `() {
        val data: Intent = mock()
        val uri: Uri = mock()
        whenever(data.data).thenReturn(uri)
        val requestFileResult =
            fileSharingHelper.extractUriFromActivityResult(REQUEST_CODE_IMAGE, RESULT_OK, data)
        requestFileResult.apply {
            assertEquals(REQUEST_RESULT_OK, result)
            assertEquals(uri, fileUri)
        }
    }

    @Throws(Exception::class)
    fun setFinalStatic(field: Field, newValue: Any) {
        field.isAccessible = true

        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

        field.set(null, newValue)
    }
}
