package com.g.laurent.alitic.Models

fun insertData(foodTypeDao:FoodTypeDao?, foodDao:FoodDao?, keywordDao:KeywordDao?){

    /** ------------------------------------------------------------------------------------------------------
    -------------------------------------- FOODTYPE INSERTION ------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // FOODTYPES
    val boissons = FoodType(null, "Boissons", null)
    val viandes = FoodType(null, "Viandes", null)
    val poissons = FoodType(null, "Poissons", null)
    val legumes = FoodType(null, "Légumes", null)
    val legumineuses = FoodType(null, "Légumineuses", null)
    val tartes = FoodType(null, "Tartes", null)
    val prod_lait = FoodType(null, "Produits laitiers", null)
    val fruits = FoodType(null, "Fruits", null)
    val sucrerie = FoodType(null, "Sucreries", null)
    val cereale = FoodType(null, "Céréales - grains", null)
    val sauces = FoodType(null, "Sauces", null)
    val autres = FoodType(null, "Autres", null)

    // INSERT FOODTYPES
    val idBoissons = foodTypeDao?.insert(boissons)
    val idViandes = foodTypeDao?.insert(viandes)
    val idPoissons = foodTypeDao?.insert(poissons)
    val idLegumes = foodTypeDao?.insert(legumes)
    val idLegumin = foodTypeDao?.insert(legumineuses)
    val idTartes = foodTypeDao?.insert(tartes)
    val idLait = foodTypeDao?.insert(prod_lait)
    val idFruits = foodTypeDao?.insert(fruits)
    val idSucre = foodTypeDao?.insert(sucrerie)
    val idCereale = foodTypeDao?.insert(cereale)
    val idSauce = foodTypeDao?.insert(sauces)
    val idAutres = foodTypeDao?.insert(autres)

    /** ------------------------------------------------------------------------------------------------------
    ----------------------------------------- FOOD INSERTION -------------------------------------------------
    ------------------------------------------------------------------------------------------------------ **/

    // BOISSONS
    val eau =foodDao?.insert(Food(null,"Eau", idBoissons,0,null))
    val cafe =foodDao?.insert(Food(null,"Café", idBoissons,0,null))
    val the =foodDao?.insert(Food(null,"Thé", idBoissons,0,null))
    val jus =foodDao?.insert(Food(null,"Jus", idBoissons,0,null))
    val choco = foodDao?.insert(Food(null,"Chocolat au lait", idBoissons,0,null))
    val soda = foodDao?.insert(Food(null,"Soda", idBoissons,0,null)) // pepsi, coca, cola, orangina, limonade
    val lait = foodDao?.insert(Food(null,"Lait", idBoissons,0,null))
    val alcool = foodDao?.insert(Food(null,"Alcools", idBoissons,0,null)) // vin, champagne, apéro, apéritif, cocktail, bière, liqueur ...

    // VIANDES
    val viande_rouge = foodDao?.insert(Food(null,"Viandes rouges", idViandes,0,null)) // agneau, boeuf, cheval, mouton
    val viande_blanche = foodDao?.insert(Food(null,"Viandes blanches", idViandes,0,null)) // lapin, porc, cochon, veau
    val viande_noire = foodDao?.insert(Food(null,"Viandes noires", idViandes,0,null)) // biche, chevreuil, sanglier, caille
    val volailles = foodDao?.insert(Food(null,"Volailles", idViandes,0,null)) // dinde, canard, pintade, poulet,
    val autres_viandes = foodDao?.insert(Food(null,"Autres viandes", idViandes,0,null)) // cordon,
    val oeuf = foodDao?.insert(Food(null,"Oeuf", idViandes,0,null))

    // POISSONS
    val crustaces = foodDao?.insert(Food(null,"Crustacés", idPoissons,0,null)) // crabe, crevette, homard, langouste, langoustine
    val fruits_mer = foodDao?.insert(Food(null,"Fruits de mer", idPoissons,0,null)) // bulot, bigorneau, coquille, huitre, huître, moule, palourde,
    val cabillaud = foodDao?.insert(Food(null,"Cabillaud", idPoissons,0,null)) // morue
    val colin = foodDao?.insert(Food(null,"Colin", idPoissons,0,null))
    val saumon = foodDao?.insert(Food(null,"Saumon", idPoissons,0,null))
    val limande = foodDao?.insert(Food(null,"Limande", idPoissons,0,null))
    val sardine = foodDao?.insert(Food(null,"Sardine", idPoissons,0,null))
    val merlan = foodDao?.insert(Food(null,"Merlan", idPoissons,0,null))
    val maquereau = foodDao?.insert(Food(null,"Maquereau", idPoissons,0,null))
    val merlu = foodDao?.insert(Food(null,"Merlu", idPoissons,0,null))
    val sole = foodDao?.insert(Food(null,"Sole", idPoissons,0,null))
    val thon = foodDao?.insert(Food(null,"Thon", idPoissons,0,null))
    val autres_poissons = foodDao?.insert(Food(null,"Autres poissons", idPoissons,0,null))

    // LEGUMES
    val avocat = foodDao?.insert(Food(null,"Avocat", idLegumes,0,null))
    val artichaut = foodDao?.insert(Food(null,"Artichaut", idLegumes,0,null))
    val chou = foodDao?.insert(Food(null,"Chou", idLegumes,0,null))
    val carotte = foodDao?.insert(Food(null,"Carotte", idLegumes,0,null))
    val celeri = foodDao?.insert(Food(null,"Céleri", idLegumes,0,null))
    val concombre = foodDao?.insert(Food(null,"Concombre", idLegumes,0,null))
    val courgette = foodDao?.insert(Food(null,"Courgette", idLegumes,0,null))
    val endive = foodDao?.insert(Food(null,"Endive", idLegumes,0,null))
    val pomme_terre = foodDao?.insert(Food(null,"Pomme de terre", idLegumes,0,null)) // frite
    val tomate = foodDao?.insert(Food(null,"Tomate", idLegumes,0,null))
    val melon = foodDao?.insert(Food(null,"Melon", idLegumes,0,null))
    val navet = foodDao?.insert(Food(null,"Navet", idLegumes,0,null))
    val oignon = foodDao?.insert(Food(null,"Oignon", idLegumes,0,null))
    val mais = foodDao?.insert(Food(null,"Maïs", idLegumes,0,null)) // mais
    val epinard = foodDao?.insert(Food(null,"Epinard", idLegumes,0,null))
    val salade = foodDao?.insert(Food(null,"Salade", idLegumes,0,null)) // laitue
    val pasteque = foodDao?.insert(Food(null,"Pastèque", idLegumes,0,null)) // pasteque
    val betterave = foodDao?.insert(Food(null,"Betterave", idLegumes,0,null))
    val poireau = foodDao?.insert(Food(null,"Poireau", idLegumes,0,null))
    val citrouille = foodDao?.insert(Food(null,"Citrouille", idLegumes,0,null))
    val radis = foodDao?.insert(Food(null,"Radis", idLegumes,0,null))

    // LEGUMINEUSES
    val lentille = foodDao?.insert(Food(null,"Lentille", idLegumin,0,null))
    val haricot = foodDao?.insert(Food(null,"Haricot", idLegumin,0,null)) // peteux
    val pois = foodDao?.insert(Food(null,"Pois", idLegumin,0,null)) // chiche

    // TARTES
    val pizza = foodDao?.insert(Food(null,"Pizza", idTartes,0,null))
    val quiche = foodDao?.insert(Food(null,"Quiche", idTartes,0,null))
    val feuillete = foodDao?.insert(Food(null,"Feuilleté", idTartes,0,null))
    val tourte = foodDao?.insert(Food(null,"Tourte", idTartes,0,null))

    // PRODUITS LAITIERS
    val fromage = foodDao?.insert(Food(null,"Fromage", idLait,0,null))
    val yaourt = foodDao?.insert(Food(null,"Yaourt", idLait,0,null))
    val beurre = foodDao?.insert(Food(null,"Beurre", idLait,0,null))
    val creme = foodDao?.insert(Food(null,"Crème", idLait,0,null))
    val glace = foodDao?.insert(Food(null,"Glace", idLait,0,null))
    val autres_lait = foodDao?.insert(Food(null,"Autres produits laitiers", idSucre,0,null))

    // FRUITS
    val compote = foodDao?.insert(Food(null,"Compote", idFruits,0,null))
    val confiture = foodDao?.insert(Food(null,"Confiture", idFruits,0,null))
    val abricot = foodDao?.insert(Food(null,"Abricot", idFruits,0,null))
    val ananas = foodDao?.insert(Food(null,"Ananas", idFruits,0,null))
    val banane = foodDao?.insert(Food(null,"Banane", idFruits,0,null))
    val cerise = foodDao?.insert(Food(null,"Cerise", idFruits,0,null))
    val clementine = foodDao?.insert(Food(null,"Clémentine", idFruits,0,null)) // mandarine, clementine
    val figue = foodDao?.insert(Food(null,"Figue", idFruits,0,null))
    val fraise = foodDao?.insert(Food(null,"Fraise", idFruits,0,null))
    val framboise = foodDao?.insert(Food(null,"Framboise", idFruits,0,null))
    val kiwi = foodDao?.insert(Food(null,"Kiwi", idFruits,0,null))
    val mangue = foodDao?.insert(Food(null,"Mangue", idFruits,0,null))
    val mure = foodDao?.insert(Food(null,"Mûre", idFruits,0,null)) // mure
    val myrtille = foodDao?.insert(Food(null,"Myrtille", idFruits,0,null))
    val orange = foodDao?.insert(Food(null,"Orange", idFruits,0,null))
    val pamplemousse = foodDao?.insert(Food(null,"Pamplemousse", idFruits,0,null))
    val peche = foodDao?.insert(Food(null,"Pêche", idFruits,0,null)) // peche, nectarine
    val poire = foodDao?.insert(Food(null,"Poire", idFruits,0,null))
    val prune = foodDao?.insert(Food(null,"Prune", idFruits,0,null))
    val pomme = foodDao?.insert(Food(null,"Pomme", idFruits,0,null))
    val raisin = foodDao?.insert(Food(null,"Raisin", idFruits,0,null))
    val citron = foodDao?.insert(Food(null,"Citron", idFruits,0,null))
    val groseille = foodDao?.insert(Food(null,"Groseille", idFruits,0,null))
    val autres_fruits = foodDao?.insert(Food(null,"Autres fruits", idFruits,0,null))

    // SUCRERIES
    val patisserie = foodDao?.insert(Food(null,"Pâtisserie", idSucre,0,null))
    val bonbon = foodDao?.insert(Food(null,"Bonbon", idSucre,0,null)) // friandise
    val chocolat = foodDao?.insert(Food(null,"Chocolat", idSucre,0,null))
    val biscuit = foodDao?.insert(Food(null,"Biscuits", idSucre,0,null))
    val miel = foodDao?.insert(Food(null,"Miel", idSucre,0,null))
    val gateau = foodDao?.insert(Food(null,"Gâteau", idSucre,0,null))
    val autres_sucre = foodDao?.insert(Food(null,"Autres sucreries", idSucre,0,null))

    // CEREALES - GRAINS
    val amande = foodDao?.insert(Food(null,"Amande", idCereale,0,null))
    val noisette = foodDao?.insert(Food(null,"Noisette", idCereale,0,null))
    val cacahuete = foodDao?.insert(Food(null,"Cacahuètes", idCereale,0,null))
    val semoule = foodDao?.insert(Food(null,"Semoule", idCereale,0,null))
    val riz = foodDao?.insert(Food(null,"Riz", idCereale,0,null))
    val noix = foodDao?.insert(Food(null,"Noix", idCereale,0,null))
    val ble = foodDao?.insert(Food(null,"Blé", idCereale,0,null))
    val olives = foodDao?.insert(Food(null,"Olives", idCereale,0,null))
    val avoine = foodDao?.insert(Food(null,"Avoine", idCereale,0,null))
    val autres_cereales = foodDao?.insert(Food(null,"Autres céréales", idCereale,0,null))

    // SAUCES
    val mayonnaise = foodDao?.insert(Food(null,"Mayonnaise", idSauce,0,null))
    val ketchup = foodDao?.insert(Food(null,"Ketchup", idSauce,0,null))
    val vinaigrette = foodDao?.insert(Food(null,"Vinaigrette", idSauce,0,null))
    val moutarde = foodDao?.insert(Food(null,"Vinaigrette", idSauce,0,null))
    val soja = foodDao?.insert(Food(null,"Soja", idSauce,0,null))
    val autres_sauces = foodDao?.insert(Food(null,"Autres sauces", idSauce,0,null))

    // AUTRES
    val pain = foodDao?.insert(Food(null,"Pain", idAutres,0,null))
    val champignon = foodDao?.insert(Food(null,"Champignon", idAutres,0,null))
    val huile = foodDao?.insert(Food(null,"Huile", idAutres,0,null))
    val algues = foodDao?.insert(Food(null,"Algues", idAutres,0,null))

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
        Keyword(null,"agneau",viande_rouge),
        Keyword(null,"boeuf",viande_rouge),
        Keyword(null,"cheval",viande_rouge),
        Keyword(null,"mouton",viande_rouge),
        Keyword(null,"lapin",viande_blanche),
        Keyword(null,"porc",viande_blanche),
        Keyword(null,"cochon",viande_blanche),
        Keyword(null,"veau",viande_blanche),
        Keyword(null,"biche",viande_noire),
        Keyword(null,"chevreuil",viande_noire),
        Keyword(null,"sanglier",viande_noire),
        Keyword(null,"caille",viande_noire),
        Keyword(null,"dinde",volailles),
        Keyword(null,"canard",volailles),
        Keyword(null,"pintade",volailles),
        Keyword(null,"poulet",volailles),
        Keyword(null,"cordon",autres_viandes),
        Keyword(null,"crabe",crustaces),
        Keyword(null,"crevette",crustaces),
        Keyword(null,"homard",crustaces),
        Keyword(null,"langouste",crustaces),
        Keyword(null,"langoustine",crustaces),
        Keyword(null,"bulot",fruits_mer),
        Keyword(null,"bigorneau",fruits_mer),
        Keyword(null,"coquille",fruits_mer),
        Keyword(null,"huitre",fruits_mer),
        Keyword(null,"huître",fruits_mer),
        Keyword(null,"moule",fruits_mer),
        Keyword(null,"palourde",fruits_mer),
        Keyword(null,"morue",cabillaud),
        Keyword(null,"frite",pomme_terre),
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



