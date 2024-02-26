# 
#
#


- tasks:
	- (recurring) show window dialogs on errors / restrictions for users, catch exceptions, etc.
	- (recurring) do tests for everything
	------------------
	- [ ] (fr, bk) notifications
	- [ ] (fr, bk) make users be able to select which notifications have what priority (default: friend requests -> high, messages -> medium, posts -> low)
	- [ ] (fr, bk) implement regions (user can have one primary region and other secondary ones) -> notifications, events, etc. only show for selected regions
	- [ ] (fr, bk) implement the chat microservice and chat rooms -> (3 types: dms, regional and custom)
	- [ ] (fr, bk) add functionality for the main page (fetching friends posts)
	- [ ] (fr) add verification through email when registering
	- [ ] (fr) add upload size cap for the file size on frontend (and update the existing ones from backend)
	- [ ] (bk) add logging instead of console prints
	- [ ] (fr) user settings
	- [ ] (bk) archive notifications (when the user clicks 'x' on frontend or when the notification is older than 30 days)
	- [ ] (fr, bk) implement search functionality
	- [ ] (fr, bk) add more functionalities to the map
	- [ ] (fr, bk) calendar
	- [ ] (fr) news section
	- [ ] (fr, bk) reset email / password
	- [ ] (bk) implement docker
	- [ ] (bk) remove pins from bk when expiration date is reached
	- [ ] (fr) do not store the token in the local storage. store it in an httpOnly cookie instead. (--> https://blog.logrocket.com/jwt-authentication-best-practices/)
	- [ ] (bk) change the return type of each controller to ResponseEntity<> to "handle" edge cases that cannot be handled through exceptions 

	_________________________________
	---------------------------------
	- [x] (fr, bk) create posts and display them on the profile
	- [x] (bk) integrate s3 as object storage service
	- [x] (fr, bk) implement friend requests
	- [x] (fr) if the user doesnt exist when accessing a profile, redirect to 404
	- [x] (bk) add a shared library
	- [x] (fr) change the register to add an unique username + password reentering
	- [x] (fr) add some general layout to the frontend pages
	- [x] (bk) rewrite the backend with new exceptions and tests
	- [x] (fr, bk) add a button for a window where the map should be, create the map microservice and connect it to the app (make db queries for saved data like markers)
	- [x] (fr) save the token locally and check for its availability when accessing the site
	- [x] (fr) redirect to main page on successful authentication
	- [x] (fr) login / register
	- [x] (bk) login / register
	- [x] add a frontend and connect it to the backend

- to add in the project documentation: 
	- create a file "credentials" in %USERPROFILE%\.aws

- task ideas:
	- app time restriction
	- calendar for events
	- users can pin their recollections on the map (probably a private map for each user)
	- news section
	- friend requests older than 1 year should be removed
	- check what would happen with entries in tables such as friendships if users would be banned or accounts would be removed


- microservices: 
	- front
	- app
	- security
	- map -> openstreetmap api
	- live chat -> kafka


- todos:
	- security
	- documentation
	- caching
		- reason1: performance (ex: db -> notifications table)
	- monitoring
	- scalability
	- performance
	- integrate design patterns
	- see where you put that calendar
	- see a weather location api for the map


