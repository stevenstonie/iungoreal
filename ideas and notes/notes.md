# 
#
#



- tasks:
	- [ ] (recurring) show window dialogs on errors for users, catch exceptions, etc.
	- [ ] (recurring) do tests for everything
	- [x] backend login / register by token
	- [x] connect the backend to the database
	- [x] frontend login / register
	- [x] (fr) redirect to main page on successful authentication
	- [ ] (fr) save the token locally and check for its availability each hour (also maybe check if when accessing)
	- [ ] (fr + bk) add a button for a window where the map should be, create the map microservice and connect it to the app
	- [ ] (fr) add verification for email
	- [ ] (fr) add logout functionality



- UI:
	- user:
		- menus
			- usual one: profile, settings, communities, etc.
			- one for moderation: settings regarding the moderation of communities (banUser(), deletePost(), etc.)



- microservices: 
	- front
	- back (70% of the the actual app)
	- map -> openstreetmap api
	- live chat -> kafka
	- marketplace -> olx api



- todos:
	- docker & kubernetes
	- logs
	- tests
	- security
	- documentation
	- caching
	- messaging
	- notifications
	- monitoring
	- scalability
	- performance
	- integrate design patterns
	- see where you put that calendar
	- see a weather location api for the map



- ideas:
	- app time restriction
	- pedometer
	- calendar for events
	- users can pin their recollections on the map (probably a private map for each user)
	- news section
	- healthy lifestyle section
	- marketplace
	- live chat



- roadmap (+notes):
	- start with the app. some models, the database, etc.
	- add a simple front end to test the features
	- add and work on the security: authentication, authorization, password encryption, etc.
	- add the chat microservice
	- continue with the app (models, db, etc.)
	- notifications