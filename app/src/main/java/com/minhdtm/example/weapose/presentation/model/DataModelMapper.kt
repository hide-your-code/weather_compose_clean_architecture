package com.minhdtm.example.weapose.presentation.model

import com.minhdtm.example.weapose.data.model.Model

interface DataModelMapper<M : Model, VD : ViewData> {
    fun mapToModel(viewData: VD): M

    fun mapToViewData(model: M): VD
}
