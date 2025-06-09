package com.example.frontendapp.data.model.UI

import com.example.frontendapp.ui.theme.composables.modals.ModalType

data class ResusableModalDTO(
    var msg: String="Sin info",
    var title: String="Sin titulo",
    var type: ModalType = ModalType.ERROR,
    var show: Boolean=false
)