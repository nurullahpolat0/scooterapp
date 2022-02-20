package com.nurullah.scooterapp.items

import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import com.nurullah.scooterapp.R
import com.nurullah.scooterapp.databinding.ItemVehicleBinding
import com.nurullah.scooterapp.models.Scooter
import com.nurullah.scooterapp.models.Scooter.Companion.TAXI

data class ScooterItem(val scooter: Scooter, val isSelected: Boolean = false) : BindableItem<ItemVehicleBinding>() {

    override fun bind(viewBinding: ItemVehicleBinding, position: Int) {
        viewBinding.vehicle = scooter

        viewBinding.imageViewLogo.setImageResource(
            R.drawable.taxi_marker.takeIf { scooter.fleetType == TAXI } ?: R.drawable.recycle_bin)
        viewBinding.textViewName.text = "Scooter".takeIf {
            scooter.fleetType == TAXI } ?: "Akıllı Çöp"
        val rnds = "%"+(0..100).random()+" Şarj"
        val rnds1 = ((0..100).random()).toString()+" Km"
        viewBinding.textViewSeats.text = rnds.takeIf {
            scooter.fleetType == TAXI } ?: rnds1
    }

    override fun bind(
        holder: ViewHolder<ItemVehicleBinding>,
        position: Int,
        payloads: MutableList<Any>,
        onItemClickListener: OnItemClickListener?,
        onItemLongClickListener: OnItemLongClickListener?
    ) {
        super.bind(holder, position, payloads, onItemClickListener, onItemLongClickListener)
        holder.binding.container.setOnClickListener { onItemClickListener?.onItemClick(this, holder.itemView) }
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return when (other) {
            is ScooterItem -> scooter.heading == other.scooter.heading
            else -> false
        }
    }

    override fun getLayout(): Int = R.layout.item_vehicle

    override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / SPANS

    companion object {
        const val SPANS = 2
    }
}