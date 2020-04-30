package kin.backupandrestore.restore.presenter

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kin.backupandrestore.restore.view.RestoreCompletedView
import org.junit.Before
import org.junit.Test


class RestoreCompletedPresenterImplTest {

    private val view: RestoreCompletedView = mock()
    private val parentPresenter: RestorePresenter = mock()

    private lateinit var presenter: RestoreCompletedPresenterImpl

    @Before
    fun setUp() {
        createPresenter()
    }

    @Test
    fun `back clicked go to previous step`() {
        presenter.onBackClicked()
        verify(parentPresenter).previousStep()
    }

    @Test
    fun `close flow with the correect account index`() {
        presenter.close()
        verify(parentPresenter).closeFlow()
    }

    private fun createPresenter() {
        presenter = RestoreCompletedPresenterImpl()
        presenter.onAttach(view, parentPresenter)
    }
}
