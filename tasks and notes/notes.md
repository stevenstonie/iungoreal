# 
#
#



- tasks:
	- (recurring) show window dialogs on errors for users, catch exceptions, etc.
	- (recurring) do tests for everything
	------------------
	- [ ] (fr) change the register to add an unique username + password reentering
	- [ ] (fr) add verification through email after registration
	- [ ] (fr) if the user doesnt exist when accessing a profile, redirect to 404
	- [ ] (fr, bk) notifications
	- [ ] (fr, bk) implement friend requests
	- [ ] (fr, bk) implement search functionality
	- [ ] (fr, bk) functionality for the main page (fetching friends posts)
	- [ ] (fr, bk) implement regions (user can have one primary region and other secondary ones) -> notifications, events, etc. only show for selected regions
	- [ ] (fr) user settings
	- [ ] (fr) user profile
	- [ ] (fr, bk) chat rooms -> (3 types: dms, regional and custom)
	- [ ] (bk) remove pins from bk when expiration date is reached
	- [ ] (fr, bk) add more functionalities to the map
	- [ ] (fr, bk) calendar
	- [ ] (fr) news section
	- [ ] (fr, bk) reset email / password
	- [ ] (fr) do not store the token in the local storage. store it in an httpOnly cookie instead. (--> https://blog.logrocket.com/jwt-authentication-best-practices/)
	- [ ] (bk) change the return type of each controller to ResponseEntity<> to "handle" edge cases that cannot be handled through exceptions 

	_________________________________
	---------------------------------
	- [x] add a frontend and connect it to the backend
	- [x] backend login / register by token
	- [x] frontend login / register
	- [x] (fr) redirect to main page on successful authentication
	- [x] (fr) save the token locally and check for its availability when accessing the site
	- [x] (fr, bk) add a button for a window where the map should be, create the map microservice and connect it to the app (make db queries for saved data like markers)
	- [x] (bk) rewrite the backend with new exceptions and tests
	- [x] (fr) add some general layout to the frontend pages


- task ideas:
	- app time restriction
	- calendar for events
	- users can pin their recollections on the map (probably a private map for each user)
	- news section


- microservices: 
	- front
	- app
	- security
	- map -> openstreetmap api
	- live chat -> kafka


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


