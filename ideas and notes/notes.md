# 
#
#



- tasks:
	- (recurring) show window dialogs on errors for users, catch exceptions, etc.
	- (recurring) do tests for everything
	- (recurring) (bk) careful with sql injection when implementing db requests for specific requests
	------------------
	- [ ] (fr) add verification through email
	- [ ] (fr, bk) notifications
	- [ ] (fr, bk) calendar
	- [ ] (fr, bk) the app per se
	- [ ] (fr, bk) live chat
	- [ ] (fr, bk) news section
	- [ ] (fr) do not store the token in the local storage. store it in an httpOnly cookie instead. (--> https://blog.logrocket.com/jwt-authentication-best-practices/)
	- [ ] (bk) rewrite the backend with new exceptions and tests
	- [ ] (bk) change the return type of each controller to ResponseEntity<> to "handle" edge cases that cannot be handled through exceptions 

	_________________________________
	---------------------------------
	- [x] add a frontend and connect it to the backend
	- [x] backend login / register by token
	- [x] frontend login / register
	- [x] (fr) redirect to main page on successful authentication
	- [x] (fr) save the token locally and check for its availability when accessing the site
	- [x] (fr, bk) add a button for a window where the map should be, create the map microservice and connect it to the app (make db queries for saved data like markers)





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
	- calendar for events
	- users can pin their recollections on the map (probably a private map for each user)
	- news section
	- healthy lifestyle section
	- marketplace
	- live chat

