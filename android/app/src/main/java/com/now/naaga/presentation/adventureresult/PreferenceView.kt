package com.now.naaga.presentation.adventureresult

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.now.domain.model.PreferenceState
import com.now.naaga.R
import com.now.naaga.databinding.CustomPreferenceViewBinding

class PreferenceView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private val binding: CustomPreferenceViewBinding
    private val layoutInflater = LayoutInflater.from(this.context)
    private var preferenceClickListener: PreferenceClickListener? = null
    var likeCount: Int = 0
        set(value) {
            field = value
            binding.tvPreferenceLikeCount.text = value.toString()
        }

    init {
        binding = CustomPreferenceViewBinding.inflate(layoutInflater, this, true)
        setClickListeners()
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PreferenceView,
            0,
            0,
        ).apply {
            val isLikeVisible = getBoolean(R.styleable.PreferenceView_isLikeVisible, true)
            val isDislikeVisible = getBoolean(R.styleable.PreferenceView_isDislikeVisible, true)
            val isLikeCountVisible = getBoolean(R.styleable.PreferenceView_isLikeCountVisible, true)
            setComponentVisibility(isLikeVisible, isDislikeVisible, isLikeCountVisible)
        }
    }

    private fun setClickListeners() {
        binding.ivPreferenceLike.setOnClickListener {
            preferenceClickListener?.onClick(PreferenceState.LIKE)
        }
        binding.ivPreferenceDislike.setOnClickListener {
            preferenceClickListener?.onClick(PreferenceState.DISLIKE)
        }
    }

    private fun setComponentVisibility(
        isLikeVisible: Boolean,
        isDislikeVisible: Boolean,
        isLikeCountVisible: Boolean,
    ) {
        val setVisibility: (Boolean) -> Int = { isVisible: Boolean -> if (isVisible) View.VISIBLE else View.GONE }
        binding.ivPreferenceLike.visibility = setVisibility(isLikeVisible)
        binding.ivPreferenceDislike.visibility = setVisibility(isDislikeVisible)
        binding.tvPreferenceLikeCount.visibility = setVisibility(isLikeCountVisible)
    }

    fun setPreferenceClickListener(listener: PreferenceClickListener) {
        preferenceClickListener = listener
    }

    fun updatePreference(preferenceState: PreferenceState) {
        when (preferenceState) {
            PreferenceState.LIKE -> {
                binding.ivPreferenceLike.isSelected = true
                binding.ivPreferenceDislike.isSelected = false
            }

            PreferenceState.DISLIKE -> {
                binding.ivPreferenceLike.isSelected = false
                binding.ivPreferenceDislike.isSelected = true
            }

            PreferenceState.NONE -> {
                binding.ivPreferenceLike.isSelected = false
                binding.ivPreferenceDislike.isSelected = false
            }
        }
    }

    fun interface PreferenceClickListener {
        fun onClick(preferenceState: PreferenceState)
    }
}
