package edu.bluejack21_2.KZkin.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.ProductDetailUserActivity
import edu.bluejack21_2.KZkin.model.Product


class ProductAdapter(private val Context: Any) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var productList: ArrayList<Product>? = ArrayList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.card_layout, viewGroup, false))
    }

    override fun getItemCount() = productList!!.size

    class ProductViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView? = itemView.findViewById(R.id.viewPosterImage)
        val productBrand: TextView? = itemView.findViewById(R.id.viewPosterName)
        val productName: TextView? = itemView.findViewById(R.id.viewPosterAge)
        val productRating: TextView? = itemView.findViewById(R.id.viewReviewRating)

        fun binding(product: Product){
            productName?.setText(product.name)
            productBrand?.setText(product.brand)
            productRating?.setText(String.format("%.1f", product.rating))
            val requestOption = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            if (productImage != null) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOption)
                    .load(product.image)
                    .into(productImage)
            }

        }
    }

    fun submitList(pList: ArrayList<Product>){
        productList = pList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newList = productList?.get(position)

        when(holder) {
            is ProductViewHolder -> {
                holder.binding(productList!!.get(position))
                holder.itemView.setOnClickListener{
                    var intent = Intent(it.context, ProductDetailUserActivity::class.java)
                    intent.putExtra("id", productList!!.get(position).id)
                    intent.putExtra("img", productList!!.get(position).image)
                    it.context.startActivities(arrayOf(intent))
                }
            }
        }
    }
}