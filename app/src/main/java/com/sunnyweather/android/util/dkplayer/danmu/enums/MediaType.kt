package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2021/1/18.
 */

enum class MediaType(val value: String) {
    LOCAL_STORAGE("local_storage"),

    OTHER_STORAGE("other_storage"),

    STREAM_LINK("stream_link"),

    MAGNET_LINK("magnet_link"),

    FTP_SERVER("ftp_server"),

    WEBDAV_SERVER("webdav_server"),

    SMB_SERVER("smb_server"),

    REMOTE_STORAGE("remote_storage");

    companion object {
        fun fromValue(value: String): MediaType {
            return when (value) {
                "local_storage" -> LOCAL_STORAGE
                "stream_link" -> STREAM_LINK
                "magnet_link" -> MAGNET_LINK
                "ftp_server" -> FTP_SERVER
                "webdav_server" -> WEBDAV_SERVER
                "smb_server" -> SMB_SERVER
                "remote_storage" -> REMOTE_STORAGE
                else -> OTHER_STORAGE
            }
        }
    }
}