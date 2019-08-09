@file:Suppress("UNUSED_VARIABLE")

package com.g.laurent.alitic.Models

import android.content.Context
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEvent
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewMeal
import com.g.laurent.alitic.R
import com.g.laurent.alitic.getDateAsLong

fun insertData(foodTypeDao:FoodTypeDao?, foodDao:FoodDao?, keywordDao:KeywordDao?, eventTypeDao:EventTypeDao?, context:Context){

    /** ------------------------------------------------------------------------------------------------------
    -------------------------------------- FOODTYPE INSERTION ------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // FOODTYPES
    val boissons = FoodType(null, "Boissons", "verre", R.color.boissons_color)
    val viandes = FoodType(null, "Viandes", "viande",R.color.viandes_color)
    val poissons = FoodType(null, "Poissons", "poisson",R.color.poissons_color)
    val legumes = FoodType(null, "Légumes", "legume",R.color.legumes_color)
    val prodlait = FoodType(null, "Laitages", "laitage",R.color.prodlait_color)
    val fruits = FoodType(null, "Fruits", "fruit",R.color.fruits_color)
    val sucrerie = FoodType(null, "Sucreries", "gateau",R.color.sucrerie_color)
    val cereale = FoodType(null, "Céréales", "cereale",R.color.cereale_color)
    val sauces = FoodType(null, "Sauces", "sauce",R.color.sauces_color)
    val autres = FoodType(null, "Autres", "autres",R.color.autres_color)

    // INSERT FOODTYPES
    val idBoissons = foodTypeDao?.insert(boissons)
    val idViandes = foodTypeDao?.insert(viandes)
    val idPoissons = foodTypeDao?.insert(poissons)
    val idLegumes = foodTypeDao?.insert(legumes)
    val idLait = foodTypeDao?.insert(prodlait)
    val idFruits = foodTypeDao?.insert(fruits)
    val idSucre = foodTypeDao?.insert(sucrerie)
    val idCereale = foodTypeDao?.insert(cereale)
    val idSauce = foodTypeDao?.insert(sauces)
    val idAutres = foodTypeDao?.insert(autres)

    /** ------------------------------------------------------------------------------------------------------
    ----------------------------------------- FOOD INSERTION -------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // BOISSONS
    val eau =foodDao?.insert(Food(null,"Eau", idBoissons,0,"eau"))
    val cafe =foodDao?.insert(Food(null,"Café", idBoissons,0,"cafe"))
    val the =foodDao?.insert(Food(null,"Thé", idBoissons,0,"the"))
    val jus =foodDao?.insert(Food(null,"Jus", idBoissons,0,"jus"))
    val choco = foodDao?.insert(Food(null,"Chocolat au lait", idBoissons,0,"choco"))
    val soda = foodDao?.insert(Food(null,"Soda", idBoissons,0,"soda")) // pepsi, coca, cola, orangina, limonade
    val lait = foodDao?.insert(Food(null,"Lait", idBoissons,0,"lait"))
    val alcool = foodDao?.insert(Food(null,"Alcools", idBoissons,0,"alcool")) // vin, champagne, apéro, apéritif, cocktail, bière, liqueur ...
    val autresBoissons = foodDao?.insert(Food(null,"Autres boissons", idBoissons,0,"boisson"))

    // VIANDES
    val viandeRouge = foodDao?.insert(Food(null,"Viandes rouges", idViandes,0,"vianderouge")) // agneau, boeuf, cheval, mouton
    val viandeBlanche = foodDao?.insert(Food(null,"Viandes blanches", idViandes,0,"viandeblanche")) // lapin, porc, cochon, veau
    val viandeNoire = foodDao?.insert(Food(null,"Viandes noires", idViandes,0,"viandenoire")) // biche, chevreuil, sanglier, caille
    val volailles = foodDao?.insert(Food(null,"Volailles", idViandes,0,"volaille")) // dinde, canard, pintade, poulet,
    val oeuf = foodDao?.insert(Food(null,"Oeuf", idViandes,0,"oeuf"))
    val autresViandes = foodDao?.insert(Food(null,"Autres viandes", idViandes,0,"viande")) // cordon,

    // POISSONS
    val crustaces = foodDao?.insert(Food(null,"Fruit de mer", idPoissons,0,"crustace")) // crabe, crevette, homard, langouste, langoustine, bulot, bigorneau, coquille, huitre, huître, moule, palourde,
    val cabillaud = foodDao?.insert(Food(null,"Cabillaud", idPoissons,0,"poisson")) // morue
    val colin = foodDao?.insert(Food(null,"Colin", idPoissons,0,"poisson"))
    val saumon = foodDao?.insert(Food(null,"Saumon", idPoissons,0,"poisson"))
    val limande = foodDao?.insert(Food(null,"Limande", idPoissons,0,"poisson"))
    val sardine = foodDao?.insert(Food(null,"Sardine", idPoissons,0,"poisson"))
    val merlan = foodDao?.insert(Food(null,"Merlan", idPoissons,0,"poisson"))
    val maquereau = foodDao?.insert(Food(null,"Maquereau", idPoissons,0,"poisson"))
    val merlu = foodDao?.insert(Food(null,"Merlu", idPoissons,0,"poisson"))
    val sole = foodDao?.insert(Food(null,"Sole", idPoissons,0,"poisson"))
    val thon = foodDao?.insert(Food(null,"Thon", idPoissons,0,"poisson"))
    val autresPoissons = foodDao?.insert(Food(null,"Autres poissons", idPoissons,0,"poisson"))

    // LEGUMES
    val avocat = foodDao?.insert(Food(null,"Avocat", idLegumes,0,"avocat"))
    val artichaut = foodDao?.insert(Food(null,"Artichaut", idLegumes,0,"artichaut"))
    val chou = foodDao?.insert(Food(null,"Chou", idLegumes,0,"chou"))
    val carotte = foodDao?.insert(Food(null,"Carotte", idLegumes,0,"carotte"))
    val celeri = foodDao?.insert(Food(null,"Céleri", idLegumes,0,"celeri"))
    val concombre = foodDao?.insert(Food(null,"Concombre", idLegumes,0,"concombre"))
    val courgette = foodDao?.insert(Food(null,"Courgette", idLegumes,0,"courgette"))
    val endive = foodDao?.insert(Food(null,"Endive", idLegumes,0,"endive"))
    val pommeTerre = foodDao?.insert(Food(null,"Pomme de terre", idLegumes,0,"pommeterre")) // frite
    val tomate = foodDao?.insert(Food(null,"Tomate", idLegumes,0,"tomate"))
    val melon = foodDao?.insert(Food(null,"Melon", idLegumes,0,"melon" ))
    val navet = foodDao?.insert(Food(null,"Navet", idLegumes,0,"navet"))
    val oignon = foodDao?.insert(Food(null,"Oignon", idLegumes,0,"oignon"))
    val mais = foodDao?.insert(Food(null,"Maïs", idLegumes,0,"mais")) // mais
    val epinard = foodDao?.insert(Food(null,"Epinard", idLegumes,0,"epinard"))
    val salade = foodDao?.insert(Food(null,"Salade", idLegumes,0,"salade")) // laitue
    val pasteque = foodDao?.insert(Food(null,"Pastèque", idLegumes,0,"pasteque")) // pasteque
    val betterave = foodDao?.insert(Food(null,"Betterave", idLegumes,0,"betterave"))
    val poireau = foodDao?.insert(Food(null,"Poireau", idLegumes,0,"poireau"))
    val citrouille = foodDao?.insert(Food(null,"Citrouille", idLegumes,0,"citrouille"))
    val radis = foodDao?.insert(Food(null,"Radis", idLegumes,0,"radis"))
    val lentille = foodDao?.insert(Food(null,"Lentille", idLegumes,0,"lentille"))
    val haricot = foodDao?.insert(Food(null,"Haricot", idLegumes,0,"haricot")) // peteux
    val pois = foodDao?.insert(Food(null,"Pois", idLegumes,0,"pois")) // chiche

    // PRODUITS LAITIERS
    val fromage = foodDao?.insert(Food(null,"Fromage", idLait,0,"fromage"))
    val yaourt = foodDao?.insert(Food(null,"Yaourt", idLait,0,"yaourt"))
    val beurre = foodDao?.insert(Food(null,"Beurre", idLait,0,"beurre"))
    val creme = foodDao?.insert(Food(null,"Crème", idLait,0,"creme"))
    val glace = foodDao?.insert(Food(null,"Glace", idLait,0,"glace"))
    val autresLait = foodDao?.insert(Food(null,"Autres prod. laitiers", idLait,0,"laitage"))

    // FRUITS
    val compote = foodDao?.insert(Food(null,"Compote", idFruits,0,"compote"))
    val confiture = foodDao?.insert(Food(null,"Confiture", idFruits,0,"confiture"))
    val abricot = foodDao?.insert(Food(null,"Abricot", idFruits,0,"abricot"))
    val ananas = foodDao?.insert(Food(null,"Ananas", idFruits,0,"ananas"))
    val banane = foodDao?.insert(Food(null,"Banane", idFruits,0,"banane"))
    val cerise = foodDao?.insert(Food(null,"Cerise", idFruits,0,"cerise"))
    val clementine = foodDao?.insert(Food(null,"Clémentine", idFruits,0,"clementine")) // mandarine, clementine
    val figue = foodDao?.insert(Food(null,"Figue", idFruits,0,"figue"))
    val fraise = foodDao?.insert(Food(null,"Fraise", idFruits,0,"fraise"))
    val framboise = foodDao?.insert(Food(null,"Framboise", idFruits,0,"framboise"))
    val kiwi = foodDao?.insert(Food(null,"Kiwi", idFruits,0,"kiwi"))
    val mangue = foodDao?.insert(Food(null,"Mangue", idFruits,0,"mangue"))
    val mure = foodDao?.insert(Food(null,"Mûre", idFruits,0,"mure")) // mure
    val myrtille = foodDao?.insert(Food(null,"Myrtille", idFruits,0,"myrtille"))
    val orange = foodDao?.insert(Food(null,"Orange", idFruits,0,"orange"))
    val pamplemousse = foodDao?.insert(Food(null,"Pamplemousse", idFruits,0,"pamplemousse"))
    val peche = foodDao?.insert(Food(null,"Pêche", idFruits,0,"peche")) // peche, nectarine
    val poire = foodDao?.insert(Food(null,"Poire", idFruits,0,"poire"))
    val prune = foodDao?.insert(Food(null,"Prune", idFruits,0,"prune"))
    val pomme = foodDao?.insert(Food(null,"Pomme", idFruits,0,"pomme"))
    val raisin = foodDao?.insert(Food(null,"Raisin", idFruits,0,"raisin"))
    val citron = foodDao?.insert(Food(null,"Citron", idFruits,0,"citron"))
    val groseille = foodDao?.insert(Food(null,"Groseille", idFruits,0,"groseille"))
    val autresFruits = foodDao?.insert(Food(null,"Autres fruits", idFruits,0,"fruit"))

    // SUCRERIES
    val patisserie = foodDao?.insert(Food(null,"Pâtisserie", idSucre,0,"patisserie"))
    val bonbon = foodDao?.insert(Food(null,"Bonbon", idSucre,0,"bonbon")) // friandise
    val chocolat = foodDao?.insert(Food(null,"Chocolat", idSucre,0,"chocolat"))
    val biscuit = foodDao?.insert(Food(null,"Biscuits", idSucre,0,"biscuit"))
    val miel = foodDao?.insert(Food(null,"Miel", idSucre,0,"miel"))
    val autresSucre = foodDao?.insert(Food(null,"Autres sucreries", idSucre,0,"gateau"))

    // CEREALES - GRAINS
    val amande = foodDao?.insert(Food(null,"Amande", idCereale,0,"amande"))
    val noisette = foodDao?.insert(Food(null,"Noisette", idCereale,0,"noisette"))
    val cacahuete = foodDao?.insert(Food(null,"Cacahuètes", idCereale,0,"cacahuete"))
    val semoule = foodDao?.insert(Food(null,"Semoule", idCereale,0,"semoule"))
    val riz = foodDao?.insert(Food(null,"Riz", idCereale,0,"riz"))
    val noix = foodDao?.insert(Food(null,"Noix", idCereale,0,"noix"))
    val ble = foodDao?.insert(Food(null,"Blé", idCereale,0,"ble"))
    val olives = foodDao?.insert(Food(null,"Olives", idCereale,0,"olive"))
    val avoine = foodDao?.insert(Food(null,"Avoine", idCereale,0,"avoine" ))
    val autresCereales = foodDao?.insert(Food(null,"Autres céréales", idCereale,0,""))

    // SAUCES
    val mayonnaise = foodDao?.insert(Food(null,"Mayonnaise", idSauce,0,"mayonnaise"))
    val ketchup = foodDao?.insert(Food(null,"Ketchup", idSauce,0,"ketchup"))
    val vinaigrette = foodDao?.insert(Food(null,"Vinaigrette", idSauce,0,"vinaigrette"))
    val moutarde = foodDao?.insert(Food(null,"Moutarde", idSauce,0,"moutarde"))
    val soja = foodDao?.insert(Food(null,"Soja", idSauce,0,"soja"))
    val autresSauces = foodDao?.insert(Food(null,"Autres sauces", idSauce,0,""))

    // AUTRES
    val pain = foodDao?.insert(Food(null,"Pain", idAutres,0,"pain"))
    val champignon = foodDao?.insert(Food(null,"Champignon", idAutres,0,"champignon"))
    val huile = foodDao?.insert(Food(null,"Huile", idAutres,0,"huile"))

    /** ------------------------------------------------------------------------------------------------------
    -------------------------------------- KEYWORDS INSERTION ------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // KEYWORDS
    val listKeywords = listOf(
        Keyword(null,"pepsi", soda),
        Keyword(null,"coca",soda),
        Keyword(null,"cola",soda),
        Keyword(null,"orangina", soda),
        Keyword(null,"limonade", soda),
        Keyword(null,"vin",alcool),
        Keyword(null,"champagne",alcool),
        Keyword(null,"apero",alcool),
        Keyword(null,"aperitif",alcool),
        Keyword(null,"cocktail",alcool),
        Keyword(null,"biere",alcool),
        Keyword(null,"bière",alcool),
        Keyword(null,"apéro",alcool),
        Keyword(null,"apéro",alcool),
        Keyword(null,"liqueur",alcool),
        Keyword(null,"agneau",viandeRouge),
        Keyword(null,"boeuf",viandeRouge),
        Keyword(null,"cheval",viandeRouge),
        Keyword(null,"mouton",viandeRouge),
        Keyword(null,"lapin",viandeBlanche),
        Keyword(null,"porc",viandeBlanche),
        Keyword(null,"cochon",viandeBlanche),
        Keyword(null,"veau",viandeBlanche),
        Keyword(null,"biche",viandeNoire),
        Keyword(null,"chevreuil",viandeNoire),
        Keyword(null,"sanglier",viandeNoire),
        Keyword(null,"caille",viandeNoire),
        Keyword(null,"dinde",volailles),
        Keyword(null,"canard",volailles),
        Keyword(null,"pintade",volailles),
        Keyword(null,"poulet",volailles),
        Keyword(null,"cordon",autresViandes),
        Keyword(null,"crabe",crustaces),
        Keyword(null,"crevette",crustaces),
        Keyword(null,"homard",crustaces),
        Keyword(null,"langouste",crustaces),
        Keyword(null,"langoustine",crustaces),
        Keyword(null,"bulot",crustaces),
        Keyword(null,"bigorneau",crustaces),
        Keyword(null,"coquille",crustaces),
        Keyword(null,"huitre",crustaces),
        Keyword(null,"huître",crustaces),
        Keyword(null,"moule",crustaces),
        Keyword(null,"palourde",crustaces),
        Keyword(null,"morue",cabillaud),
        Keyword(null,"frite",pommeTerre),
        Keyword(null,"mais",mais),
        Keyword(null,"laitue",salade),
        Keyword(null,"pasteque",pasteque),
        Keyword(null,"peteux",haricot),
        Keyword(null,"potiron",citrouille),
        Keyword(null,"chiche",pois),
        Keyword(null,"creme",creme),
        Keyword(null,"mandarine",clementine),
        Keyword(null,"clementine",clementine),
        Keyword(null,"mure",mure),
        Keyword(null,"peche",peche),
        Keyword(null,"nectarine",peche),
        Keyword(null,"patisserie",patisserie),
        Keyword(null,"friandise",bonbon),
        Keyword(null,"gateau",patisserie),
        Keyword(null,"Cacahuete",cacahuete)
    )

    keywordDao?.insertAll(listKeywords)

    /** ------------------------------------------------------------------------------------------------------
    -------------------------------------- EVENT TYPE INSERTION ----------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    val reflux = eventTypeDao?.insert(EventType(null, "Reflux", "reflux", null, null, true))
    val malVentre = eventTypeDao?.insert(EventType(null, "Mal au ventre", "mal_ventre", 0, 3*60*60*1000, false))
    val malTete = eventTypeDao?.insert(EventType(null, "Mal à la tête", "tete", 0, 3*60*60*1000, false))
    val diarrh = eventTypeDao?.insert(EventType(null, "Diarrhée", "diarrhee", 0, 3*60*60*1000, false))
    val balon = eventTypeDao?.insert(EventType(null, "Ballonnement", "ballonnement", 0, 3*60*60*1000, false))
    val brulure = eventTypeDao?.insert(EventType(null, "Brûlure d'estomac", "brulure_estomac", 0, 3*60*60*1000, false))
    val constip = eventTypeDao?.insert(EventType(null, "Constipation", "constipation", 0, 3*60*60*1000, false))
    val vomis = eventTypeDao?.insert(EventType(null, "Vomissement", "vomissement", 0, 3*60*60*1000, false))

    fun insertAdditionnalDataForTest(){
        /** ------------------------------------------------------------------------------------------------------
        ------------------------------------------ MEAL INSERTION ------------------------------------------------
        ------------------------------------------------------------------------------------------------------ **/

        val list1 : MutableList<Long?> = mutableListOf(alcool,bonbon,volailles)
        val list2 : MutableList<Long?> = mutableListOf(alcool,bonbon,crustaces,chocolat)
        val list3 : MutableList<Long?> = mutableListOf(pain, moutarde, patisserie, lait)
        val list4 : MutableList<Long?> = mutableListOf(alcool,viandeRouge,bonbon,volailles,pain,chocolat)
        val list5 : MutableList<Long?> = mutableListOf(haricot, lait, crustaces, peche)
        val list6 : MutableList<Long?> = mutableListOf(pain,olives,patisserie,endive)
        val list7 : MutableList<Long?> = mutableListOf(viandeRouge,chocolat,crustaces,lait)
        val list8 : MutableList<Long?> = mutableListOf(lait,peche,pain,olives)
        val list9 : MutableList<Long?> = mutableListOf(haricot,bonbon,pain,crustaces)
        val list10 : MutableList<Long?> = mutableListOf(lait,chocolat,endive,viandeRouge)


        val idMeal1 = saveNewMeal(list1, getDateAsLong(10, 2, 2019, 12, 0),
            false, context)
        val idMeal2 = saveNewMeal(list2, getDateAsLong(11, 2, 2019, 12, 0),
            false,context)
        val idMeal3 = saveNewMeal(list3, getDateAsLong(12, 2, 2019, 12, 0),
            false, context)
        val idMeal4 = saveNewMeal(list4,getDateAsLong(12, 2, 2019, 20, 0),
            false, context)
        val idMeal5 = saveNewMeal(list5,getDateAsLong(13, 2, 2019, 12, 0),
            false,context)
        val idMeal6 = saveNewMeal(list6,getDateAsLong(14, 2, 2019, 12, 0),
            false,context)
        val idMeal7 = saveNewMeal(list7,getDateAsLong(15, 2, 2019, 12, 0),
            false,context)
        val idMeal8 = saveNewMeal(list8,getDateAsLong(16, 2, 2019, 12, 0),
            false,context)
        val idMeal9 = saveNewMeal(list9,getDateAsLong(16, 2, 2019, 20, 0),
            false,context)
        val idMeal10 = saveNewMeal(list10,getDateAsLong(17, 2, 2019, 12, 0),
            false,context)
        val idMeal11 = saveNewMeal(list8,getDateAsLong(11, 6, 2019, 12, 0),
            false,context)
        val idMeal12 = saveNewMeal(list3,getDateAsLong(11, 6, 2019, 21, 0),
            false,context)
        val idMeal13 = saveNewMeal(list5,getDateAsLong(17, 6, 2019, 12, 0),
            false,context)
        val idMeal14 = saveNewMeal(list4,getDateAsLong(18, 6, 2019, 12, 0),
            false,context)
        val idMeal15 = saveNewMeal(list7,getDateAsLong(18, 6, 2019, 19, 0),
            false,context)

        /** ------------------------------------------------------------------------------------------------------
        ------------------------------------------ EVENT INSERTION -----------------------------------------------
        ------------------------------------------------------------------------------------------------------ **/

        val idEvent1 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(10, 2, 2019, 12, 30),
            context = context)
        val idEvent2 = saveNewEvent(idEventType = malTete,dateCode = getDateAsLong(11, 2, 2019, 12, 30),
            context = context)
        val idEvent3 = saveNewEvent(idEventType = reflux, dateCode = getDateAsLong(12, 2, 2019, 20, 30),
            context = context)
        val idEvent4 = saveNewEvent(idEventType = malTete, dateCode = getDateAsLong(13, 2, 2019, 13, 0),
            context = context)
        val idEvent5 = saveNewEvent(idEventType = malVentre,dateCode = getDateAsLong(14, 2, 2019, 13, 0),
            context = context)
        val idEvent6 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(15, 2, 2019, 13, 0),
            context = context)
        val idEvent7 = saveNewEvent(idEventType = malVentre,dateCode = getDateAsLong(16, 2, 2019, 20, 0),
            context = context)
        val idEvent8 = saveNewEvent(idEventType = malVentre,dateCode = getDateAsLong(17, 2, 2019, 13, 0),
            context = context)
        val idEven9 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(11, 3, 2019, 13, 0),
            context = context)
        val idEvent10 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(15, 3, 2019, 13, 0),
            context = context)
        val idEvent11 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(22, 3, 2019, 13, 0),
            context = context)
        val idEvent12 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(8, 4, 2019, 13, 0),
            context = context)
        val idEvent13 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(16, 4, 2019, 13, 0),
            context = context)
        val idEvent14 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(28, 4, 2019, 13, 0),
            context = context)
        val idEvent15 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(3, 5, 2019, 13, 0),
            context = context)
        val idEvent16 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(19, 5, 2019, 13, 0),
            context = context)
        val idEvent17 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(19, 5, 2019, 13, 0),
            context = context)
        val idEvent18 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(19, 5, 2019, 13, 0),
            context = context)
        val idEvent19 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(3, 6, 2019, 13, 0),
            context = context)
        val idEvent20 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(17, 6, 2019, 13, 0),
            context = context)
        val idEvent21 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(17, 6, 2019, 18, 0),
            context = context)
        val idEvent22 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(18, 6, 2019, 13, 0),
            context = context)
        val idEvent23 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(11, 6, 2019, 13, 0),
            context = context)
        val idEvent24 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(11, 6, 2019, 15, 0),
            context = context)
        val idEvent25 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(12, 6, 2019, 13, 0),
            context = context)
        val idEvent26 = saveNewEvent(idEventType = malVentre,dateCode = getDateAsLong(10, 7, 2019, 16, 0),
            context = context)
        val idEvent27 = saveNewEvent(idEventType = reflux,dateCode = getDateAsLong(11, 7, 2019, 13, 0),
            context = context)

    }

    insertAdditionnalDataForTest()
}




