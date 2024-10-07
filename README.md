# Team Engineering Project - wr(a)ite
Mobile application for the development of writing skills using artificial intelligence for text creation and analysis created as part of the Team Engineering Project course. The Team Engineering Project constituted the final project at the end of my engineering degree.
-
The goal of this project was to develop a mobile application that enables users to enhance their literary skills.
It serves as an accessible tool to encourage the regular development of a writing hobby by suggesting challenges and offering the ability to compete with others. 
The use of artificial intelligence elements contributes to the application's attractiveness for users.

The main feature of the app is allowing users to complete tasks that involve writing texts containing randomly selected words. 
Submitted texts are be analyzed using intelligent algorithms for accuracy. 
In a competitive setting, users are able to compare texts written for the same challenges.
Additionally, users are be able to compete against texts generated using artificial intelligence. 
The app also collects user statistics, such as the number of published stories, average story length (in number of worst) and best streak, showing their level of engagement.
The final texts are be available for all users to read in the app's library.

As part of the project, a mobile application and a relational database were designed. The designed mobile application was implemented as a native mobile application for Android.
To analyse and evaluate users' texts the Naive Bayes Classifier, JSpell Checker and Text Sentiment Analysis were used. 
Each story is checked for correct spelling. Additionally, each text is classified into a genre to assess whether the user has correctly completed the challenge. Furthermore, the overall emotional tone of each text is evaluated.
A generative approach is used to generate stories for the minigame.
To allow for communication between the database and applications an API was designed and implemented in Python.

The database and API were deployed with Heroku.

The main branch contains the project documentation. The mobile app can be found in the mobile_app branch. The api branch contains the Python API.
-
This project was completed in collaboration with [A. Kłobus](https://github.com/AgnieszkaKlobus12), [B. Tlołka](https://github.com/Boguslawa-Tlolka) and with minimal contributions by [A. Koperwas](https://github.com/olakoperwas).
