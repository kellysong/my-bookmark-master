package com.sjl.bookmark.ui.activity

import com.sinpo.xnfc.NFCardActivity

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyNfcActivity
 * @time 2021/9/19 11:48
 * @copyright(C) 2021 song
 */
class MyNfcActivity : NFCardActivity() {
    override fun getLaunchMainClass(): Class<*> {
        return MainActivity::class.java
    }
}