package io.github.diov.syncpreferance

import android.database.MatrixCursor
import android.os.Bundle

/**
 * Created by Dio_V on 2019-05-28.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

class ExtraCursor(private var content: Bundle) : MatrixCursor(emptyArray(), 0) {

    override fun getExtras(): Bundle {
        return content
    }

    override fun respond(extras: Bundle?): Bundle {
        content = extras ?: return content
        return content
    }
}
