package lofitsky.komptube.local.ui

import javafx.scene.Scene
import lofitsky.komptube.local.ui.ServiceController.LocalServiceController
import lofitsky.komptube.local.ui.ServiceController.PhoneServiceController


object HealthCheckOnClickListeners {
    private var scene: Scene? = null

    private val listenerFactory by lazy { HealthCheckButtonOnClickListenerFactory(scene!!) }

    val kompHealthCheckOnClickListener by lazy {
        listenerFactory.create(
            LocalServiceController,
            "#kompBackendHealthCheck",
            "img/komp_active32.png",
            "img/komp_inactive32.png",
        )
    }

    val phoneHealthCheckOnClickListener by lazy {
        listenerFactory.create(
            PhoneServiceController,
            "#phoneBackendHealthCheck",
            "img/phone_active32.png",
            "img/phone_inactive32.png",
        )
    }

    fun setScene(scene: Scene): Unit = if(this.scene == null) this.scene = scene else {}
}
