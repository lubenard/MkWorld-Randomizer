package com.escatrag.mkworldrandomiser

import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScoreViewModelTest {

    @Test
    fun migrateAvatarRes_null_returnNull() {
        assertNull(ScoreViewModel.migrateAvatarRes(null))
    }

    @Test
    fun migrateAvatarRes_validDrawable_unchanged() {
        assertEquals(R.drawable.mario, ScoreViewModel.migrateAvatarRes(R.drawable.mario))
        assertEquals(R.drawable.bowser_jr, ScoreViewModel.migrateAvatarRes(R.drawable.bowser_jr))
        assertEquals(R.drawable.stingby, ScoreViewModel.migrateAvatarRes(R.drawable.stingby))
    }

    @Test
    fun migrateAvatarRes_orphanCircuitDrawable_returnNull() {
        assertNull(ScoreViewModel.migrateAvatarRes(R.drawable.circuit_mario))
        assertNull(ScoreViewModel.migrateAvatarRes(R.drawable.mont_tchou_tchou))
    }
}