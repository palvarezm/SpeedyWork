package com.example.speedywork.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.speedywork.R
import com.example.speedywork.adapters.CardStackAdapter
import com.example.speedywork.models.Spot
import com.example.speedywork.utils.SpotDiffCallback
import com.google.android.material.navigation.NavigationView
import com.yuyakaido.android.cardstackview.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class JobCardsActivity : AppCompatActivity(), CardStackListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createSpots()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_cards)
        setupNavigation()
        setupCardStackView()
        setupButton()
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        //TODO: Change to a better logic to find a match
        if (manager.topPosition >= adapter.itemCount-6){
            val cardView = findViewById<View>(R.id.itemCard)
            val textView = cardView.findViewById<TextView>(R.id.item_name)
            MaterialAlertDialogBuilder(this).setPositiveButton("Aceptar", null).create().apply {
                setTitle("Exito")
                setMessage("Conseguiste un trabajo")
                show()
            }

        }
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }


    private fun setupNavigation() {
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.reload -> reload()
                R.id.add_spot_to_first -> addFirst(1)
                R.id.add_spot_to_last -> addLast(1)
                R.id.remove_spot_from_first -> removeFirst(1)
                R.id.remove_spot_from_last -> removeLast(1)
                R.id.replace_first_spot -> replace()
                R.id.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun paginate() {
        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getSpots()
        val new = createSpots()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addFirst(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createSpot())
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addLast(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            addAll(List(size) { createSpot() })
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeLast(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun replace() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createSpot())
        }
        adapter.setSpots(new)
        adapter.notifyItemChanged(manager.topPosition)
    }

    private fun swap() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createSpot(): Spot {
        return Spot(
            name = "Yasaka Shrine",
            city = "Kyoto",
            url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
        )
    }

    private fun createSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        spots.add(Spot(name = "Italgreen", city = "Publicista Senior", url = "https://www.italgreen.es/computedimage/sede-italgreen-erba-sintetica-crusnigo.i4767-kpN6mSc-w1000-h1000-l1-n1.jpg"))
        spots.add(Spot(name = "Repsol", city = "Programador BI", url = "data:image/jpeg;base64,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"))
        spots.add(Spot(name = "ESVICSAC", city = "Diseñador Gráfico", url = "https://i.ytimg.com/vi/llcRpOPmr80/hqdefault.jpg"))
        spots.add(Spot(name = "BCP", city = "Analista Programador Java", url = "https://img.gestion.pe/files/ec_article_multimedia_gallery/uploads/2017/11/06/5a007d547bcec.jpeg"))
        spots.add(Spot(name = "AJE Group", city = "Diseñador Gráfico", url = "https://scontent.flim5-1.fna.fbcdn.net/v/t1.0-9/26219954_1563541673766043_5443182504050549798_n.png?_nc_cat=110&_nc_oc=AQm5Rtg4kf0QpMaCVIv9ssJ0cY8nJM0feRpM1wBVxXacLMMS3gMCRkYvElWm0gvXByA&_nc_ht=scontent.flim5-1.fna&oh=773398b5686d782baebdfee7c900b040&oe=5D89C915"))
        spots.add(Spot(name = "Antamina", city = "Publicista Junior", url = "https://is1-ssl.mzstatic.com/image/thumb/Purple114/v4/32/ad/42/32ad427d-d5ac-6d89-b369-f5af6b288dbb/AppIcon-0-1x_U007emarketing-0-0-GLES2_U002c0-512MB-sRGB-0-0-0-85-220-0-0-0-5.png/1200x630wa.png"))
        spots.add(Spot(name = "Tasa", city = "Desarrollador Python", url = "data:image/png;base64,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"))
        spots.add(Spot(name = "Tebca", city = "Publicista Junior", url = "https://yt3.ggpht.com/a-/AAuE7mDIu3hcWByvSiyTkbpvCBLgz22UR5c07lDh2g=s900-mo-c-c0xffffffff-rj-k-no"))
        spots.add(Spot(name = "Atento", city = "Diseñador Gráfico", url = "https://infotechlead.com/wp-content/uploads/2015/07/Atento-Logo.jpg"))
        spots.add(Spot(name = "Inteligo", city = "Programador .Net Junior", url = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxISEhUTEhQVFhUXGBgXFRUVGBUYFhUYGBgWGhYXFRYYHiggGBolHRkXITEhKCorLi4uGB8zOTMtNygtLisBCgoKDg0OGhAQGjElHyYtLi0tNy4tKy0vLS0tLy0tLS0tLS01LSstLS8vLSstLS0tLS0tLS0tLSstLS0tLS0tLf/AABEIAPkAygMBIgACEQEDEQH/xAAcAAEAAgIDAQAAAAAAAAAAAAAABgcEBQIDCAH/xABJEAABAwICBwQFBwoEBgMAAAABAAIDBBEFEgYHITFBUWETMnGBInKRobEUNUJSgrPBFSM0U2JzkqKy0TNjdPAkQ0TCw9IXk6P/xAAZAQADAQEBAAAAAAAAAAAAAAAAAgMEAQX/xAAtEQADAAIBAwEIAgEFAAAAAAAAAQIDERIhMUETBCIyM1FhcYGh0UIUNFKxwf/aAAwDAQACEQMRAD8AvFERABERABERABERABFrsYx2mpW5qiVkfIE+k71WD0neQUExbW9C3ZTwPk/akIjaeoAu4+YCecdV2QlZJnuyzEVHVmtLEJO52MQ4ZGZneZeSD7Frn6aYi/fVSfZDG/0tCr/pq8k/XnwegkXn2PSmvH/VTebr/FZ9NpxiTf8AqC7o5kZ9+W/vXH7PX1GWVfQvNFVFDrPqW/40MbxzZmYffmHwUpwrWHRTEB5dC7/MHo/xtuAPGyR4qQ6tMlyLhFK1wDmkOadoIIII6Eb1zUxgiIgAiIgAiIgAiIgAiIgAiIgAiKPaY6WQ4fFmf6cjr9nEDYu6k/RaOJ+K6lvojjeja4rikNNGZZ3tYwcTxPANA2uPQbVU2lGtOaW7KMGFm7tHAGV3gNoYPafBQ3SDH562XtJ3XO3K0bGRg8GN4eO88StYtEYku5Grb7HKeVz3F73Fznd5ziS4+JO0rrXJFoVaM9Rs+tfZdgnXVZLJuSF4Uuxktq13xV4WvsvllzcsZc0SGnrWHeFsoqSKTcbFQ0LvgrHtOwpXG+zHWTXxIm1CKujOeneQN5aNrHesw7D471OdG9P4piI6kCGTcHX/ADbj4nuHofaq3wXSe1g/aOqk0mGQVbM0ZAco3P8AyReWn1llrIqswHSWfD3CCpDnwbgd7ox+zzb+z7ORs6mqGSMa9jg5rhdrhtBB5LPUuSieztRESnQiIgAiIgAiIgAiLpq6lkTHSSENYxpc5x3BoFyfYgDUaYaSR0FOZXWc8+jFHexe/wDBo3k/iQvPuK4lLUyummdme7eeAHBrRwaOAWfpdpFJX1DpnXDB6MTPqM4faO8nn0AWlWrHHFELrYREVCYQlSLRPQ2przeMBkQNnTPBy34hg+m7pu5kK4tGdBqOiALWdpLxlks532BuZ5beZKSsikeYbKXwnRCuqReKnfl+u+0bT1BeRmHhdSOm1S1zu/JAzpme4jyDLe9XWii8rKLGimptUNUO5PA71u0b8AVr6zVdiLBdrYpOkcm3/wDQNV6ouerR300eYcSwqenOWeKSM8M7SAfVdud5ErDXqaogZI0te1rmne1wBB8QdhVe6U6rIZAX0Z7GTf2ZuYndBxj8rjoqTmXkR4ym1tsGxt8LhYrDxLD5aeR0UzCx7d7Xe4g7iDzGxYpWhUmtMg5ae5Lgo6qGviyutmtsPFYeA4tLhk/ZTXNO47eOQn6bfxHnv317guKuheCCrLc+Oug4ZwNijccengvF819yy43hwDmkEEAgjaCDuIPELkq71c46Y3mhmPMwk+10fxI8xyViLPU8XoqnsIiJToREQAREQAVW65dIiA2ijPeAkmI+rf0GeZGY+Deas6omaxjnuNmtBc48gBcn2LzPjmJuqqiWofvkcXW+q3cxvk0NHkq4p29iZHpaMFERakjO2FNtXehBrXdtMCKZptbaDM4b2tPBo4nyG25Gi0RwF1dUshFw3vSuH0Ix3j4nYB1IXoqipGQxtijaGsYA1rRuACllvXRD4531Zzp4GsaGMaGtaLNa0AAAbgANwXYiLKaAiIgAi66idrGlzzYDeVoZMce8/mxlbzO1x/AJlLYlZJnuSJFpYJ5T9I+5Z0VUR3/aPxQ5BWma/SvRiCviySCzxfs5QPSjP4tPFvHxsRQGN4TLSTOgmFnN4juuadz2ni0/3G8FemwonrF0WFdTksH5+IF0R4u+tGfWts6gdU+O9dGFzvqUCVItFcYMTwL7FHVyjflIK1r3lpmVvi+SLC0iYQWVERs4EOBG8OBuD7VaejeLCrp45hsLhZw+q8bHD27uhCqDC6ztYSw8lI9VWJFk0tK47HjtGeu2wcB4tsfsLPc+7+DSn139Sz0RFnKBERABERAER1qYl2GHyAH0piIW/auX/wAjXqg1bGvGq9Gli5mSQ/ZDWj+pyqdacS90hb6hfF9K7KOmMsjIhsMj2sB5F7g0H3rQuxCn1Ls1RYH2FH27h6dQQ+/ERjZGPA7XfbU6XVSwNjY1jRZrGhrRyDRYD2BdqwVXJ7NsrS0EREp0IiIAhmlWIF83ZA+jHYnq4i/uBHtK40JWmxiQisnB35/cQCPdZZ1FOtjnUo8mMvLJTf1ZKqMhZNS4WWkgql2yVag56m5WtG1w2e928t3h/v4rOWhwOTNKfVPxC3ySlplcdbkoXWnggpq1zmi0c4MreQde0g/i9L7ah5V1658PD6Js1tsMjdv7MnoEfxZPYqUWrFW0iORG4wGqymy3FDXfJ6uGa9g17S4/sk2f/KSotQvs5bSrdcKtT1EivdPR6LX6PVPa0sEhNy6KNxPUtF/etgvOa0bAiIgAiIgCmtdrv+KgHKG/te7+yrpWHrsaflkJ4GAAeT33+IVeLXj+FGe+4K3ugUIfiNK0/rQ7zYHPHvaFpIxdw8VI9BPQxSmv+sI/ijeB7yrP4GQ/zR6FREXnG8LDqMTjYbE3PJu0jx4BarSXFywiGM2cRdxG9oO4Dqf971rKFqrOPptme8/vcZJNHibT9F3u/usuKUO3FaykiBXfNHl2jYUjSKKnrqRDWDhbmPFUwXaQGy24EbGuPQjZ5DmtHR1vVWnE5srCCAQbhzTtB5gg7wohimgQLi6mkyX/AOW+5b5OG0D2rVizTx42eX7T7Jkm3lw9U+6/9Rr4qxcn1qRaHVl7Ew255nfDKpFg2irIiHyu7R42gWswHw4nx9iKqF5O4pzX046/JlaNUZZGXuFnPsbHeGjdfrtv7FuERZG9vZ6sTxWkR3WHCH4bVA8Iy7zYQ8e9oXndejNPX2w6r/cvH8Qt+K85rRh7E8vc5QnaFsnP2LVs3hZpdsWtoyw+5ferx98Opz+yR7HuH4KRKNat2Ww2n6h59sjz+Kkq82/iZun4UEREowREQBU2vGnOelk4WkYfEFhH/cquV464aAyUGcb4ZGPPqm7D/WD5KjlqxP3SF9znCfSHituJuwqaefcGSRvJ6NcCfddaVSGeDtqbMN4Vt9iWu56Iab7Qvqi+rbGPlNBESbviHYyc7sAAJ8W5T5qULBS09GuXtbK0rqovqZif1j2+TSWj3BbSilWk0jhMFZIDuee0aeYftP8ANmXfSVS3VO5TR4mLLq6Vd02S+mqLLsnqrhR+KsXJ9Ys/A9BZlokGDTXc8dAVtlotF2Eh8h3Eho8t/wDvot6pX3NGJ7lMIiJSgREQBCdb1aI8Ocy+2V8bB5O7Q+5nvVFlWFrlxjtallO0+jA27v3kljY+Dcv8RVeFbMM9DNlrqfW713OfsXQDtWVh1GaiaOFu+R7WXHDMQCfIXPktRkT+h6J0OpjFQ0zDvEMd/EtBPvJW4XFjQAANwFh4Bcl5Te3s9NLS0ERFw6EREAYmLULZ4JYXbpGOYemYEX8t68y1VO6J7o3iz2OLHDk5pIPvC9Sql9cOAGKoFU0ehPYP6StH/c0X8WuVsVddE8i8leqQ6J1QzGJ24qPLnDKWODhvC09+hHs9k+0TxT8mV5ZIbU89muPBjr+g/wAASQejr8FdKpSONmIU2XZ2jRsUg1daXlhFBWG0jfRgkd9McI3H6w4Hju374ZJ5dfK7lJfF68PsTLSbAGVcdicsjdsb+R4g/slVvV009K7LMwt5O3sd4OGz8VcK+EX2Fcx53C0+qM/tPsM5q5y9V/3+UVFHiY5hSDB8ImnILgWR8XOFifVB3+O5ThlJG03DGA8w0A+1dyas+/hQuL2Fp+/W/wBaOuCFrGhrRYAWAXYiLMegEREAFqNKsdZRUz532JAsxv13nut/E8gCeCzsRr44I3SzODGNF3OP4czwA4qgdN9KX4hPm2thZcQsPAcXO/aPuFh1NIjkxLrSNDVVL5XukkOZ73FzjzLjcrpK+ota6GZ9TjZTzU9g/bVhnI9CnbfxkeC1o9mc+QUFXoLVxgJo6JjXi0sh7WQcQXAZWnwaGjxulzZNToMWJctkoREWI2BERABERABazSPBmVlPJA/YHjY7ixw2tcPA2+C2aIA8v4nQSU8r4ZRZ7DlcOHQjmCLEHkQsVXlrK0N+WR9tCP8AiIxsH61g25D+0NuU9SONxRxBBsQQRsIOwgjeCOBWuK5Iz1OmZuEYm+neHNOziFNaylgxGLM0gSge1V4smhrnwuzMNuidrfVdxU9dH2LJ0a0+moyKfEQ5zBsbOAXPaP8AMG9467/FWhQ1sUzBJC9sjDucwgj2jj0VJQY5DVNyTgX58VjR0M9K8y0U7mX35TsPIObud5gqdY1X2f8AA6prt1X8l/Iqfw/WnWRejVQMkH1mXjd4ne0nwAW+ptblG7vxTsPgxw9odf3KTw2vA6yw/JYSKCu1rYeN3bHoI/7uWsrdcMIv2NNK48O0cxg/lzLnpX9DvqT9SzVoNJtLqWhae1feS3owssZDyuPojqbBVHjOsqvnu1r2wMPCEWdbrIbm/UWUQc4kkkkkm5J2knmSd5VJwP8AyJvMvBv9LNLKivfeQ5Y2m8cTe63qT9J1uJ62AutAiK+kuiJ7bCIttoxgEtdO2GPYN8j7bI2cXHrwA4nzI43o6kSHVXoyaqp7eRv5mAg7dz5d7G9bbHH7PNXmsPCMMipoWQwtysYLDmebnHi4naT1WYsl1yZomdIIiJBgiIgAiIgAiIgAq+1h6ACqvUUoAn+mzYGzdb7hJ13HjzVgouzTT2jjWzyxPC5jnMe0tc02c1wILSN4IO4rgvQml2hVPXjM783MBZszRt6B43Pb7xwIVMaS6J1VCT2zLx32TMuYzyufonobdLrTNqiNS0aJZdPiMjNztnIrERV2T0bX8sk95t1jS1EbvorDRdTSONNn2QjhdcV9Rd5i+mfLL6iJXQykIvrGkkAAkk2AAJJPAADeVYOiWrGaYiSsvDFv7P8A5r+h/Vjx29BvSVSXcdLfYi2i+jc9fL2cQs0f4khHoRjrzdybvPQXIvzRvAIaKERQjq9578juLnHn03DgszDqCKCMRQsaxjdzWjZ4nmep2lZKz3botM6CIimOEREAEREAEREAEREAEREAF8e0EEEAg7CDtBHIhfUQBCMd1Y0U5Los1O8/q9sZPWM7B4NyqB4pqur4rmMRzt4ZHZXW6tfYDwBKvNE6yUhXCZ5jrcGqYb9rBMy28ujeG/xWsfasDOOYXqtdMtJG7vMYfFoPxCp632E9P7nlrMOa5QtLzlYC48A0Ek+QXp1mFU42iGIHpGz+yyY4mt7oA8AB8Eet9g9P7nnSh0PxCa2SllseL29mPG8ltimODaopXWNVM1g+pCMzvN7gAD5FW6iV5WxljRpNHtFKSiH5iMZ+MjvSkP2juHQWHRbtEU29j6CIi4AREQAREQAREQAREQAREQAREQAREQAREQAREQAWtxnHqakymolEee+W4cb2tfcDzC2SqzXp3aT1pfhGqYoV2pZPLbiW0WThmIxVEbZYXh8br2cL2NiQd/UFZSh+qX5sh9aX716mCW5400ND5SmEREowREQAREQAREQAREQBhYzisVLC6eZxbG3LmIBcRmcGjY0EnaQsXR7SWmrg80zy4MIDrte21727wF9xWo1sfNc/jD9/Eo3qL7lX60XwerLGnid+dkXkayKCdaQ6S01CGGpeWh5IbZr3Xy2v3Qbbwuo6X0Qpm1RmDYXFwaXBwc4tJBDWEZibg7AFCtencpPWl+DFqtX2gvy2Nk9U9xgbmbDE11ifTOe5+g3Nm2DaTxGy7zij01dMSst+o4lEuj1rYeXZT2zR9cx+j7AS73KZUNZHMxskT2vY4Xa5puCq+0v1aUgppJKRjo5Y2l4Gd7xIGi5aQ8mxIGwi21aTUnizmzyUxPoPYZGjgHtLQbeLTt9UIeOKh1HgJyXNqb8kv1gaXwU8U1N2j2VDoiY8gfsLgQ0h47puDxVd6t9JoqWpllq5H2fHlzHPI4uzg7bXO66nesjQ6CZk9a58gkjgcWtaW5D2Yc4XBbfeearrV5o1FiE8kUrntDY84MZaDfM0bcwOzaq4lj9J/wAk8rv1V/BbWFafUFRKyGKRxkeSGgxyAEgEnaRYbAVKFCsD1a0tLPHUMknLoySA4x5TdpbtswHceawNb+kr6eJlNC4tfMCXuBsWxjZYcsxuL8muWdxNWpgurqYdWbrGdYWH0zix0pkeNhbEM9jxBd3b9LrHw3Wbh0zg0vfETuMzcrfN4JaPEkKIaBat2VELairLgx+2OJhy3bwc92/bwAtssb7bDP0y1ZU8dO+alLmPjaXFjnFzXgC5ALtodbdttw43FOGFPjt7J88zXLS0Wi1wIuNoO4jiqt16d2k9aX4RrnqX0gc9r6ORxORvaQ34MuA9ngCWkeseAXDXp3aT1pfhGuY4cZlLO5LV4W0SPVL82Q+tL969bDHtM6KjkEVRI5ry0PsGSO9ElwBu0Eb2la/VL82Q+tL969V9rq/T2/6dn9cyFCvM0/uDtxiTX2Ltpp2yMa9hu1zQ5p5hwuDt6FdGJYnDTszzyMjbzeQLnkL7z0C6NHTajpz/AJEX3bVR4+UY3iFs1g4uLSdrYYWng3ie74uPsTHi5N7fRD5MvFLS6stOTWXhgNu3ceoimI9uVcf/AJOwz9c//wCqb/1WlrdUEHZEQzyiUDYZMhYTyLWtBA8Ds6qGaB4VSy1jqOticXkuawte9uWSPNnY7KbEENdt5t67KrHhabTfQk8mVNJpdS9sPrGTRMljN2PaHNNiLgi42HaFjY1jtNSNzVErYwdwO1zvVaNrvILCxetiwuhLmj0IWBkTCSS47Gsbc7d9rnlcqpNHNH6nGaiSeaQhgNpJSL7d4iibuFgfAAjffbPHiVbpvSK5MjnUpbZPjrXw/Na09vrdmLezNf3KT4HpFS1gJp5Wvt3m7Q9vrMdYgdbKON1V4dly5ZSfr9o7N42Ho+5V/pbopUYTKyop5XGPNZkosHxu+pIBsIIv0O0Ec3UYr6S2n9xHeWOtJNfYvdFo9DNIBXUrJrAP2tkaPovbvt0Oxw6OC3izNNPTNCaa2iI62Pmufxh+/iUb1F9yr9aL4PUk1sfNc/jD9/Eo3qL7lX60XwetM/If5/oz18+fx/Y16dyk9aX4MUi1TfNkPrS/evUd16dyk9aX4MUi1TfNkPrS/evRX+3X5/s5Pz3+P6JPiX+DJ6jv6SqP1PfOLP3UnwCvDEv8KT1Hf0lUfqe+cWfupPgEYfl2dzfMgt7Tb5vq/wDTy/0OVZ6kf0ub9x/5GK0tLKd0lFVMYLudBKGjmSx1gqa1VY5DS1bjO4MZJGWB57rXZmuGY8AbHb4IxLeKkjmV6yy2X0qM1zEnEBf9RHbwzSfjdWjX6cYdEBepieSQAInCU7ejL281EddOBOe2OsYL9mOzltwYTdjvAOLh9sLns/u2t+RvaPeh6NNQ4/jlTHHHSQujjaxrWujjDWlrQAPzs2w7OVllR6tMRqiHVtUB0c587h5Eho8iu/V5rEiihbTVhLcgyxy2Jbk4MfbaCNwO61r2ttnE+m+HNbmNXCRya7M7+Ftz7k91cPUzr9E4mLW6rf7MLRPQGnoJO1Y+V8mUtu4tDbG17NaOg3kqM69O7SetL8I1JNHNPYa2rNPDG/II3P7V5tctcwWDN9vS3kg7NyjevTu0nrS/CNLj5+sufcfJx9J8exI9UvzZD60v3r1X2ur9Pb/p2f1zKwdUvzZD60v3r1X2ur9Pb/p2f1zJsXz3+xMvyF+i39Hhejpx/kRfdtVJ4bPJgmJHtGFzW5mEbjJC4jK+MnYTsafEEbOF26OfolN+5i/oaqo1p6QvmqjRFsccUbmAyOYHPu4NJeCRdrQHDY2xNt+2yXBt058PuNn0pVeV2JdW61KBsZdGZJH22R5HNN+TnOGUDqCfNQjVfQy1WJGqcPRjdJLI4DYZJQ8Bo63eXeDeql2DarsOLWvMklQCL5g8CN3q9ntt9oqc4bh0VPGI4I2xsG5rRYX4k8z1O1ceSIlqPJ1RdtO/H0IFrvefksAG4zbfERvt+K3mq6JjcMgy/Szucebi917+FreSyNP8BNbRviZ/iNIki6vbf0fMFzfNVxq102bRZqWqzNiLiWuIN4X7nte3eGk+w357OyneHU90wp8M232aLqUe1gxNdh1UH2sInOF/rN9Jn8wCz26QUhZ2gqYMn1u1jy+26q/WZp3HUs+SUpLmEgyyAGz7G7WMG8i9iTxsALqeLHTtFMuSVLNjqMeezqm/RD4yPEtcD7g1Wionq00fdR0YEgtLK7tJBxbcANYeoaBfqSpYuZqVW2gwy1CTNRpZgny2lkps/Z5yw58ua2R7X924vfLbfxWt0F0P/JolHbdr2hae5ky5Q4fWN96lKJVdKePgdxLrl5Itp1of+UREO27Lsy49zPmzBo+sLblsdEsD+Q0rKfP2mQvOfLlvme53dubWvbetwiOdcePg4olVy8nXUxZ2Obe2ZpF+VxZQbQ7Vx8gqGz/Ke0s1zcvZZO8BtvnPwU9RE3UppeTtRNNN+Aq50k1UxTyOlp5exLiS6Mtzsud5ZYgs8No5WVjIiLqHuWcuJtapFXYVqga1wdUVJcAQckTMl7c3uJ2eQ8VZ8kYcC1wBaQQQRcEHeCDvC5Ii8lX8TCMcx8KK1xzVJDI4uppjDfb2bm52D1TcFo8brWwanZL+nVsA45YiT73CytxE69oyJa2I/Z8be9Ed0V0MpaC7og50hGV0rzdxGwkC1g0XA3DgL3WPp1of+UhEO27Lsy89zPmzZf2m2tl96lSJPUrly31HeOePHXQ0+iWB/IaVlPn7TKXnPly3zPc7u3NrXtvUe011e/lCoE/yjsrRtjy9nn7rnm984+tutwU5RCyUq5LuDxy54vsaPE8Rjw2hD5CXiGNkYtsMjgA1oA4XPjYX5KIxRUGPgu7OaGeNoDpANg5NL7Fj+gNnb7WW51r4ZJPQO7MFxje2UtG0ua24dYcbB1/JQ3VdppTUkT6eoJZd5kbKGlzTmDQQ/KCQRl2HdY8LbbY59x3PxbI5K99TXw6ONZq2xGkJfRT5+No3ugkPlmyn+JY2D6w6+jl7OsDpGtNnskaGzMHNrrC54+le/MXurLk06w1ouaqK3S7j/CBdVHrBx1mI1bDTMcQGiJhtZ8ri4kWbvttsAdu9VxusnTJP7JZFOPrFfovumnbIxr2G7XNDmkbi1wuCPIqLaWaAUtcTJtimO+RgHpcu0YdjvHYeq3uj9EYKWCFxu6OJjHW3Xa0A26XWwWNU5e5ZscqlqkU+dTk2b9Kjtz7N1/Zm/FS7RTV1S0bhK4maYbnvADWnmyPbY9SSRwUyRUrPkpabEnBEvaQREUSoREQAREQAREQAREQAREQAREQAREQAREQAUSxzV3QVLi8sdE87S6EhtzxJaQW362UtRNNOeqYtSq6NFct1P0l9s9RbleIH25FJtHtDaOiOaGL85u7R5L39bE93yAUgRNWW6WmxZxRL2kERFMoEREAEREAf/9k="))
        return spots
    }
}
