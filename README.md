# Th¹ Android Code Assignment

### Introduction
- This code assignment of an Android application is intended to test a Th¹ Senior Android Engineer capabilities.

### Requested tasks
- Pick an app at initial stage and add some functionalities.
1) Below the balance we should show a list of the last 10 transactions.
2) They should be saved locally so the user can see it when is offline and should refresh when connection is back.
3) Implement styles provided in a [Figma doc](https://www.figma.com/file/gc7NONoPrghg2sVwItLu6f/Formula-Money?type=design&node-id=1%3A2&mode=design&t=jayHJnsOxRog2r49-1).

Stretch Goals: Add an In-App Purchase for Premium Feature.

Use provided [API service](https://8kq890lk50.execute-api.us-east-1.amazonaws.com/prd/api) and load responses in files embedded in the app when API service is down.

Estimated coding time: 8 hours

---

## Formula Money App

https://github.com/mauriciolima1988/th1_android_code_assignment/assets/106693466/8ca8b2a5-55c1-4efe-8c9a-c565b3cbd46a

### Features
#### 💵 Load account balance
#### 📝 Show latest transactions
#### 🗄 Cache last response locally to be used in case of missing network connection
#### 📶 Refreshs data if request fails and network come back later
#### 🏅 Offer In-App Subscription to get Premium insights about your finances

### Developer notes

#### In-App Subscription
- For testing purposes we added a backdoor to simulate the launch and return of a Google Play In-app purchase, thus making the subscription successfull just by dismissing the error at the bottom sheet that'll appear.
- To subscribe for Premium just tap on the offer and dismiss the bottom sheet. Tap the insight to simulate the subscription cancelled.
- We renamed the package name so we were able to upload the app to Play Console, thus enabling the In-app Billing API connection.
- This implementation of an In-App Subscription is just for demonstration purposes and does not reflect the security that would be a final stage of the app in production.

#### Network requests & Caching
- Following [Swagger requests](https://8kq890lk50.execute-api.us-east-1.amazonaws.com/prd/api) provided, we added data class models and updated the ApiService interface to add two more methods: `getTransactions` and `getAdvice`.
- For caching responses we choosed to save the json response file in the app cache directory, and later loading that same response if network fails.
- Only if the network fails for the first the app is open, then it will load the pre-compiled .json files as responses. If it fails after the first response successfull, then it loads the cached response.
- We are registering a broadcast listener to listen when the connection is back again, and if any request has failed, it will then request again.
- In order to make the API request to work, we needed to add a `ModifiedOkHttpClient` to accept non trusted certificates.

#### Layouts
- For layout design we are using XML files.

#### Design pattern
- As there was already a ViewModel in the app, we procced to use MVVM as design pattern.
- The implementation of the MVVM here is very basic, only what is necessary for us to fit in an 8-hour test.

#### Bottlenecks
- We could add support for Dark Mode.
- We should add unit and automation tests.

#### Libraries used on this project
* ViewModel
* Billing Client
* Flow
* Coroutines
* Lifecycle
* Retrofit
* Gson
* OkHttp

---
##### Mauricio Lima
---
### License

MIT
