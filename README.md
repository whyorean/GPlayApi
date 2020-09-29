# GPlayAPI

Google Play Store Protobuf API wrapper in Kotlin

## Build

    git clone https://gitlab.com/AuroraOSS/gplayapi.git
    gradlew :assemble
    gradlew :build

## Work Flow
 1. Obtain AASToken from (Email,Password) pair.
 2.  Obtain AuthData from (Email,AASToken) pair.
 3. Use AuthData to access data.

## Usage
### AASToken
Use one of the following tools
* [Authenticator](https://github.com/whyorean/Authenticator)
* [AASTokenGrabber](https://github.com/whyorean/AASTokenGrabber)

### AuthData 

    val authData = AuthHelper.build(email,aastoken)

### Fetch App Details

    val app = AppDetailsHelper
    .with(authData)
    .getAppByPackageName(packageName)

### Fetch Bulk App Details (Max 20)

    val appList = AppDetailsHelper
    .with(authData)
    .getAppByPackageName(packageNameList)

### Fetch APKs/OBBs/Patches

    val files = PurchaseHelper
    .with(authData)
    .purchase(app.packageName,app.versionCode,app.offerType)

### Fetch All Categories

    val categoryList = CategoryHelper
    .with(authData)
    .getAllCategoriesList(type) //type = GAME or APPLICATION

### Fetch Search Suggestions

    val entries = SearchHelper
    .with(authData)
    .searchSuggestions(query)

### Search Apps & Games

    var helper = SearchHelper.with(authData)
    var appList = helper.searchResults(query, null)  
	while (helper.hasNext()) { 
	    appList = helper.next()  
	}

