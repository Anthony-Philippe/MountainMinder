package fr.isen.derkrikorian.skimouse.composables

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.Network.Lift
import fr.isen.derkrikorian.skimouse.Network.Slope
import fr.isen.derkrikorian.skimouse.Network.SlopeDifficulty

@Composable
fun observeSlopes(slopes: MutableList<Slope>, slopesReference: DatabaseReference): List<Slope> {
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var slopeIdCounter = 0
            slopes.clear()
            for (slopeSnapshot in dataSnapshot.children) {
                val slope = slopeSnapshot.getValue(Slope::class.java)
                slope?.id = slopeIdCounter
                if (slope != null) {
                    slopes.add(slope)
                }
                slopeIdCounter += 1
            }
            slopes.sortBy { SlopeDifficulty.fromString(it.color ?: "").value }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }

    LaunchedEffect(slopesReference) {
        slopesReference.addValueEventListener(listener)
    }

    DisposableEffect(slopesReference) {
        onDispose {
            slopesReference.removeEventListener(listener)
        }
    }

    return slopes
}

@Composable
fun observeLifts(lifts: MutableList<Lift>, liftsReference: DatabaseReference): List<Lift> {
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var liftIdCounter = 0
            lifts.clear()
            for (liftSnapshot in dataSnapshot.children) {
                val lift = liftSnapshot.getValue(Lift::class.java)
                lift?.id = liftIdCounter
                if (lift != null) {
                    lifts.add(lift)
                }
                liftIdCounter += 1
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }

    LaunchedEffect(liftsReference) {
        liftsReference.addValueEventListener(listener)
    }

    DisposableEffect(liftsReference) {
        onDispose {
            liftsReference.removeEventListener(listener)
        }
    }

    return lifts
}