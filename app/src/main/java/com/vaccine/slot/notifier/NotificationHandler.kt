package com.vaccine.slot.notifier

//
//class NotificationHandler(private val application: Application) : OneSignal.OSNotificationOpenedHandler {
//    /*
//        HANDLE THE PUSH NOTIFICATION
//     */
//    override fun notificationOpened(result: OSNotificationOpenResult?) {
//        result?.notification?.payload?.launchURL.let {
//            val builder = CustomTabsIntent.Builder()
//            val customTabsIntent = builder
//                .setShowTitle(true)
//                .setToolbarColor(application.resources.getColor(R.color.purple_700))
//                .build()
//            customTabsIntent.launchUrl(application, Uri.parse(it))
//        }
//    }
//}