package com.vaccine.slot.notifier.other

import androidx.core.content.FileProvider


class GenericFileProvider : FileProvider() {
    /*
    If your targetSdkVersion >= 24, then we have to use FileProvider class to give access
     to the particular file or folder to make them accessible for other apps. We create our own
     class inheriting FileProvider
     */
}