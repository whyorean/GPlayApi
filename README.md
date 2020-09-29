# GPlayAPI

Google Play Store Protobuf API wrapper in Kotlin

**For Educational & Research purpose only

## Disclaimer

I'm not resposible for anything that may go wrong with:
1. You 
2. Your Google Account.
3. Socio-economic Life, especially the nudes.
4. Girlfriend.
5. Crypto Wallet.

***Hold your own beer!

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

## Credits
1. [googleplay-api](https://github.com/egirault/googleplay-api)
2. [google-play-crawler](https://github.com/Akdeniz/google-play-crawler)
3. [play-store-api](https://github.com/yeriomin/play-store-api)
4. [raccon4](https://github.com/onyxbits/raccoon4)
