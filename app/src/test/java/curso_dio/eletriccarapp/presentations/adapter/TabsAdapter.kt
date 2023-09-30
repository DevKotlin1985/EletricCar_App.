package curso_dio.eletriccarapp.presentations.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import curso_dio.eletriccarapp.presentations.CarFragment
import curso_dio.eletriccarapp.presentations.FavoriteFragment

class TabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Fragment {
       return when(position){
           0 -> CarFragment()
           1 -> FavoriteFragment()
           else -> CarFragment()

       }
    }

    override fun createFragment(position: Int): Fragment {
      return 2
    }
}