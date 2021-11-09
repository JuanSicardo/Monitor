package com.juansicardo.monitor.bloodpressure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.juansicardo.monitor.R
import com.juansicardo.monitor.home.HomeViewModel
import com.juansicardo.monitor.profile.Profile

class BloodPressureFragment : Fragment() {


    //Extract data from parent activity
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.blood_pressure_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Extract data from parent activity
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            this.profile = profile
        }
    }
}