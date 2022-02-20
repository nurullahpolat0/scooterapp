package com.nurullah.scooterapp.items

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.databinding.BindableItem
import com.nurullah.scooterapp.R
import com.nurullah.scooterapp.databinding.ItemExpandableHeaderBinding

class ExpandableHeaderItem(private val title: String, private val size: Int) :
    BindableItem<ItemExpandableHeaderBinding>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewBinding: ItemExpandableHeaderBinding, position: Int) {
        viewBinding.title = "${title.lowercase()} ($size)"
        viewBinding.root.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewBinding.imageViewIcon.setImageResource(getToggleIcon())
        }
    }

    override fun getLayout(): Int = R.layout.item_expandable_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getToggleIcon(): Int {
        return if (expandableGroup.isExpanded) R.drawable.ic_round_expand_less else R.drawable.ic_round_expand_more
    }
}