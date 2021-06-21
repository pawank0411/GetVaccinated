package com.vaccine.slot.notifier.other

object Constants {
    const val EXTRA_TITLE_BOOK = "Title"
    const val EXTRA_MESSAGE_ERROR = "Message"
    const val EXTRA_TITLE_SUBSCRIBE = "SubscribeTitle"
    const val EXTRA_MESSAGE_SUBSCRIBE = "SubscribeMessage"
    const val EXTRA_MESSAGE_UNSUBSCRIBE = "UnSubscribe"
    const val EXTRA_MESSAGE_SNACK = "SnackMessage"
    const val EXTRA_ACTION_SNACK = "Action"

    const val ACTION_NOTIFICATION = "Open Settings"
    const val ACTION_INSTALL = "Install"

    const val DEFAULT_MESSAGE_ERROR = "Please provide the necessary details"
    const val QUOTE_VACCINE = "some motivational quote"
    const val APP_TAG = "#GetVaccinated"
    const val CO_WIN_LINK = "https://selfregistration.cowin.gov.in/"

    const val FEE_PAID = "Paid"
    const val FEE_FREE = "Free"

    const val BOOK_APPOINTMENT_TAG = "BookAppointmentDialog"
    const val CRITERIA_TAG = "CriteriaDialog"
    const val DISTRICT_TAG = "DistrictDialog"
    const val STATE_TAG = "StateDialog"
    const val ERROR_TAG = "ErrorDialog"
    const val SUBSCRIBE_DIALOG = "SubscribeDialog"
    const val UNSUBSCRIBE_DIALOG = "UnSubscribeDialog"
    const val CONNECTION_DIALOG = "ConnectionDialog"
    const val SNACK_DIALOG = "SnackDialog"

    const val SUCCESS_SUBSCRIBED = "Added to subscriptions."
    const val SUCCESS_UNSUBSCRIBED = "Removed from subscriptions."

    const val TAB_SEARCH_BY_DISTRICT = "District"
    const val TAB_SEARCH_BY_PIN_CODE = "Pin"

    const val UPDATED_APK_FILE_NAME = "getVaccinated.apk"

    private const val VACCINE_1 = "Covaxin"
    private const val VACCINE_2 = "Covishield"
    private const val VACCINE_3 = "Sputnik V"

    val MAP_VACCINE = mapOf(
            "1" to VACCINE_1,
            "2" to VACCINE_2,
            "3" to VACCINE_3
    )

    fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target.toString().contains(it.value.toString(), ignoreCase = true) }.keys.first()
    }
}


