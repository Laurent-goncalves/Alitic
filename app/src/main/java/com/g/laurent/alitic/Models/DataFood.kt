@file:Suppress("UNUSED_VARIABLE")

package com.g.laurent.alitic.Models

fun insertData(foodTypeDao:FoodTypeDao?, foodDao:FoodDao?, keywordDao:KeywordDao?){

    /** ------------------------------------------------------------------------------------------------------
    -------------------------------------- FOODTYPE INSERTION ------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // FOODTYPES
    val boissons = FoodType(null, "Boissons", "verre")
    val viandes = FoodType(null, "Viandes", "viande")
    val poissons = FoodType(null, "Poissons", "poisson")
    val legumes = FoodType(null, "Légumes", "legume")
    val tartes = FoodType(null, "Tartes", "tarte")
    val prodlait = FoodType(null, "Laitages", "laitage")
    val fruits = FoodType(null, "Fruits", "fruit")
    val sucrerie = FoodType(null, "Sucreries", "gateau")
    val cereale = FoodType(null, "Céréales", "cereale")
    val sauces = FoodType(null, "Sauces", "sauce")
    val autres = FoodType(null, "Autres", "autres")

    // INSERT FOODTYPES
    val idBoissons = foodTypeDao?.insert(boissons)
    val idViandes = foodTypeDao?.insert(viandes)
    val idPoissons = foodTypeDao?.insert(poissons)
    val idLegumes = foodTypeDao?.insert(legumes)
    val idTartes = foodTypeDao?.insert(tartes)
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
    val eau =foodDao?.insert(Food(null,"Eau", idBoissons,0,"eau", false))
    val cafe =foodDao?.insert(Food(null,"Café", idBoissons,0,"cafe", false))
    val the =foodDao?.insert(Food(null,"Thé", idBoissons,0,"the", false))
    val jus =foodDao?.insert(Food(null,"Jus", idBoissons,0,"jus", false))
    val choco = foodDao?.insert(Food(null,"Chocolat au lait", idBoissons,0,"choco", false))
    val soda = foodDao?.insert(Food(null,"Soda", idBoissons,0,"soda", false)) // pepsi, coca, cola, orangina, limonade
    val lait = foodDao?.insert(Food(null,"Lait", idBoissons,0,"lait", false))
    val alcool = foodDao?.insert(Food(null,"Alcools", idBoissons,0,"alcool", false)) // vin, champagne, apéro, apéritif, cocktail, bière, liqueur ...

    // VIANDES
    val viandeRouge = foodDao?.insert(Food(null,"Viandes rouges", idViandes,0,"vianderouge", false)) // agneau, boeuf, cheval, mouton
    val viandeBlanche = foodDao?.insert(Food(null,"Viandes blanches", idViandes,0,"viandeblanche", false)) // lapin, porc, cochon, veau
    val viandeNoire = foodDao?.insert(Food(null,"Viandes noires", idViandes,0,"viandenoire", false)) // biche, chevreuil, sanglier, caille
    val volailles = foodDao?.insert(Food(null,"Volailles", idViandes,0,"volaille", false)) // dinde, canard, pintade, poulet,
    val autresViandes = foodDao?.insert(Food(null,"Autres viandes", idViandes,0,"viande", false)) // cordon,
    val oeuf = foodDao?.insert(Food(null,"Oeuf", idViandes,0,"oeuf", false))

    // POISSONS
    val crustaces = foodDao?.insert(Food(null,"Fruit de mer", idPoissons,0,null, false)) // crabe, crevette, homard, langouste, langoustine, bulot, bigorneau, coquille, huitre, huître, moule, palourde,
    val cabillaud = foodDao?.insert(Food(null,"Cabillaud", idPoissons,0,null, false)) // morue
    val colin = foodDao?.insert(Food(null,"Colin", idPoissons,0,null, false))
    val saumon = foodDao?.insert(Food(null,"Saumon", idPoissons,0,null, false))
    val limande = foodDao?.insert(Food(null,"Limande", idPoissons,0,null, false))
    val sardine = foodDao?.insert(Food(null,"Sardine", idPoissons,0,null, false))
    val merlan = foodDao?.insert(Food(null,"Merlan", idPoissons,0,null, false))
    val maquereau = foodDao?.insert(Food(null,"Maquereau", idPoissons,0,null, false))
    val merlu = foodDao?.insert(Food(null,"Merlu", idPoissons,0,null, false))
    val sole = foodDao?.insert(Food(null,"Sole", idPoissons,0,null, false))
    val thon = foodDao?.insert(Food(null,"Thon", idPoissons,0,null, false))
    val autresPoissons = foodDao?.insert(Food(null,"Autres poissons", idPoissons,0,null, false))

    // LEGUMES
    val avocat = foodDao?.insert(Food(null,"Avocat", idLegumes,0,null, false))
    val artichaut = foodDao?.insert(Food(null,"Artichaut", idLegumes,0,null, false))
    val chou = foodDao?.insert(Food(null,"Chou", idLegumes,0,null, false))
    val carotte = foodDao?.insert(Food(null,"Carotte", idLegumes,0,null, false))
    val celeri = foodDao?.insert(Food(null,"Céleri", idLegumes,0,null, false))
    val concombre = foodDao?.insert(Food(null,"Concombre", idLegumes,0,null, false))
    val courgette = foodDao?.insert(Food(null,"Courgette", idLegumes,0,null, false))
    val endive = foodDao?.insert(Food(null,"Endive", idLegumes,0,null, false))
    val pommeTerre = foodDao?.insert(Food(null,"Pomme de terre", idLegumes,0,null, false)) // frite
    val tomate = foodDao?.insert(Food(null,"Tomate", idLegumes,0,null, false))
    val melon = foodDao?.insert(Food(null,"Melon", idLegumes,0,null, false))
    val navet = foodDao?.insert(Food(null,"Navet", idLegumes,0,null, false))
    val oignon = foodDao?.insert(Food(null,"Oignon", idLegumes,0,null, false))
    val mais = foodDao?.insert(Food(null,"Maïs", idLegumes,0,null, false)) // mais
    val epinard = foodDao?.insert(Food(null,"Epinard", idLegumes,0,null, false))
    val salade = foodDao?.insert(Food(null,"Salade", idLegumes,0,null, false)) // laitue
    val pasteque = foodDao?.insert(Food(null,"Pastèque", idLegumes,0,null, false)) // pasteque
    val betterave = foodDao?.insert(Food(null,"Betterave", idLegumes,0,null, false))
    val poireau = foodDao?.insert(Food(null,"Poireau", idLegumes,0,null, false))
    val citrouille = foodDao?.insert(Food(null,"Citrouille", idLegumes,0,null, false))
    val radis = foodDao?.insert(Food(null,"Radis", idLegumes,0,null, false))
    val lentille = foodDao?.insert(Food(null,"Lentille", idLegumes,0,null, false))
    val haricot = foodDao?.insert(Food(null,"Haricot", idLegumes,0,null, false)) // peteux
    val pois = foodDao?.insert(Food(null,"Pois", idLegumes,0,null, false)) // chiche

    // TARTES
    val pizza = foodDao?.insert(Food(null,"Pizza", idTartes,0,null, false))
    val quiche = foodDao?.insert(Food(null,"Quiche", idTartes,0,null, false))
    val feuillete = foodDao?.insert(Food(null,"Feuilleté", idTartes,0,null, false))
    val tourte = foodDao?.insert(Food(null,"Tourte", idTartes,0,null, false))

    // PRODUITS LAITIERS
    val fromage = foodDao?.insert(Food(null,"Fromage", idLait,0,null, false))
    val yaourt = foodDao?.insert(Food(null,"Yaourt", idLait,0,null, false))
    val beurre = foodDao?.insert(Food(null,"Beurre", idLait,0,null, false))
    val creme = foodDao?.insert(Food(null,"Crème", idLait,0,null, false))
    val glace = foodDao?.insert(Food(null,"Glace", idLait,0,null, false))
    val autresLait = foodDao?.insert(Food(null,"Autres produits laitiers", idSucre,0,null, false))

    // FRUITS
    val compote = foodDao?.insert(Food(null,"Compote", idFruits,0,null, false))
    val confiture = foodDao?.insert(Food(null,"Confiture", idFruits,0,null, false))
    val abricot = foodDao?.insert(Food(null,"Abricot", idFruits,0,null, false))
    val ananas = foodDao?.insert(Food(null,"Ananas", idFruits,0,null, false))
    val banane = foodDao?.insert(Food(null,"Banane", idFruits,0,null, false))
    val cerise = foodDao?.insert(Food(null,"Cerise", idFruits,0,null, false))
    val clementine = foodDao?.insert(Food(null,"Clémentine", idFruits,0,null, false)) // mandarine, clementine
    val figue = foodDao?.insert(Food(null,"Figue", idFruits,0,null, false))
    val fraise = foodDao?.insert(Food(null,"Fraise", idFruits,0,null, false))
    val framboise = foodDao?.insert(Food(null,"Framboise", idFruits,0,null, false))
    val kiwi = foodDao?.insert(Food(null,"Kiwi", idFruits,0,null, false))
    val mangue = foodDao?.insert(Food(null,"Mangue", idFruits,0,null, false))
    val mure = foodDao?.insert(Food(null,"Mûre", idFruits,0,null, false)) // mure
    val myrtille = foodDao?.insert(Food(null,"Myrtille", idFruits,0,null, false))
    val orange = foodDao?.insert(Food(null,"Orange", idFruits,0,null, false))
    val pamplemousse = foodDao?.insert(Food(null,"Pamplemousse", idFruits,0,null, false))
    val peche = foodDao?.insert(Food(null,"Pêche", idFruits,0,null, false)) // peche, nectarine
    val poire = foodDao?.insert(Food(null,"Poire", idFruits,0,null, false))
    val prune = foodDao?.insert(Food(null,"Prune", idFruits,0,null, false))
    val pomme = foodDao?.insert(Food(null,"Pomme", idFruits,0,null, false))
    val raisin = foodDao?.insert(Food(null,"Raisin", idFruits,0,null, false))
    val citron = foodDao?.insert(Food(null,"Citron", idFruits,0,null, false))
    val groseille = foodDao?.insert(Food(null,"Groseille", idFruits,0,null, false))
    val autresFruits = foodDao?.insert(Food(null,"Autres fruits", idFruits,0,null, false))

    // SUCRERIES
    val patisserie = foodDao?.insert(Food(null,"Pâtisserie", idSucre,0,null, false))
    val bonbon = foodDao?.insert(Food(null,"Bonbon", idSucre,0,null, false)) // friandise
    val chocolat = foodDao?.insert(Food(null,"Chocolat", idSucre,0,null, false))
    val biscuit = foodDao?.insert(Food(null,"Biscuits", idSucre,0,null, false))
    val miel = foodDao?.insert(Food(null,"Miel", idSucre,0,null, false))
    val gateau = foodDao?.insert(Food(null,"Gâteau", idSucre,0,null, false))
    val autresSucre = foodDao?.insert(Food(null,"Autres sucreries", idSucre,0,null, false))

    // CEREALES - GRAINS
    val amande = foodDao?.insert(Food(null,"Amande", idCereale,0,"amande", false))
    val noisette = foodDao?.insert(Food(null,"Noisette", idCereale,0,"noisette", false))
    val cacahuete = foodDao?.insert(Food(null,"Cacahuètes", idCereale,0,"cacahuete", false))
    val semoule = foodDao?.insert(Food(null,"Semoule", idCereale,0,"semoule", false))
    val riz = foodDao?.insert(Food(null,"Riz", idCereale,0,"riz", false))
    val noix = foodDao?.insert(Food(null,"Noix", idCereale,0,"noix", false))
    val ble = foodDao?.insert(Food(null,"Blé", idCereale,0,"ble", false))
    val olives = foodDao?.insert(Food(null,"Olives", idCereale,0,"olive", false))
    val avoine = foodDao?.insert(Food(null,"Avoine", idCereale,0,"avoine", false))
    val autresCereales = foodDao?.insert(Food(null,"Autres céréales", idCereale,0,"", false))

    // SAUCES
    val mayonnaise = foodDao?.insert(Food(null,"Mayonnaise", idSauce,0,null, false))
    val ketchup = foodDao?.insert(Food(null,"Ketchup", idSauce,0,null, false))
    val vinaigrette = foodDao?.insert(Food(null,"Vinaigrette", idSauce,0,null, false))
    val moutarde = foodDao?.insert(Food(null,"Moutarde", idSauce,0,null, false))
    val soja = foodDao?.insert(Food(null,"Soja", idSauce,0,null, false))
    val autresSauces = foodDao?.insert(Food(null,"Autres sauces", idSauce,0,null, false))

    // AUTRES
    val pain = foodDao?.insert(Food(null,"Pain", idAutres,0,null, false))
    val champignon = foodDao?.insert(Food(null,"Champignon", idAutres,0,null, false))
    val huile = foodDao?.insert(Food(null,"Huile", idAutres,0,null, false))
    val algues = foodDao?.insert(Food(null,"Algues", idAutres,0,null, false))

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
        Keyword(null,"chiche",pois),
        Keyword(null,"feuillete",feuillete),
        Keyword(null,"creme",creme),
        Keyword(null,"mandarine",clementine),
        Keyword(null,"clementine",clementine),
        Keyword(null,"mure",mure),
        Keyword(null,"peche",peche),
        Keyword(null,"nectarine",peche),
        Keyword(null,"patisserie",patisserie),
        Keyword(null,"friandise",bonbon),
        Keyword(null,"gateau",gateau),
        Keyword(null,"Cacahuete",cacahuete)
    )

    keywordDao?.insertAll(listKeywords)
}



