package lofitsky.komptube.local.ui

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import lofitsky.komptube.local.ui.HealthCheckOnClickListeners.kompHealthCheckOnClickListener
import lofitsky.komptube.local.ui.HealthCheckOnClickListeners.phoneHealthCheckOnClickListener
import lofitsky.komptube.local.ui.ServiceController.PhoneServiceController


class KomptubeController {
    @FXML
    private lateinit var urlField: TextField

    @FXML
    private fun onUrlFieldKeyReleased(event: KeyEvent) {
        if(event.code == KeyCode.ENTER) onShareToPhoneButtonClick()
    }

    @FXML
    private fun onShareToPhoneButtonClick() {
        PhoneServiceController.sendForOpening(urlField.text)
    }

    @FXML
    fun onKompBackendHealthCheckClick() {
        kompHealthCheckOnClickListener.handle()
    }

    @FXML
    fun onPhoneBackendHealthCheckClick() {
        phoneHealthCheckOnClickListener.handle()
    }
}
