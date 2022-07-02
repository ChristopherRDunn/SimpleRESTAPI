package com.example.assignment;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Manages endpoints
 * This is a basic REST API using Spring Boot to remove some tedious parts
 * Can use basic CRUD operations to save, retrieve, delete, and update user information
 * See ../resources/sampleRequestValues.txt for example request urls and data
 */
@RestController
public class UserDataController {
    private final UserService userService;
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    public UserDataController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Basic root endpoint
     * @return Response entity with valid userData if they exist, or a bad request response */
    @RequestMapping("/")
    public String index() {
        // TODO: Add something interesting here
        return "index";
    }

    /**
     * Endpoint to get all users data from db
     * @return Response entity with valid userData if they exist, or a bad request response */
    @GetMapping(value="/users")
    public ResponseEntity<List<UserData>> getAllUsers() {
        // TODO: Remove the password from returned fields, generally not great to pass it around everywhere
        List<UserData> userList = userService.findAll();
        return new ResponseEntity<List<UserData>>(userList, HttpStatus.OK);
    }

    /**
     * Endpoint to get user data for a specific user
     * @inputs userId(from path): id of user to request data from
     * @return Response entity with valid userData if they exist, or a bad request response */ 
    @GetMapping(value="/users/{id}")
    public ResponseEntity<UserData> getAllUsers(@PathVariable("id") String userId) {
        try {
            Long id = Long.parseLong(userId);
            Optional<UserData> userData = userService.findById(id); 
            if(userData.isPresent()) {
                return new ResponseEntity<UserData>(userData.get(), HttpStatus.OK);
            }
        } catch (NumberFormatException e) {} // Catch will have same behaviour as bad userData

        return new ResponseEntity<UserData>(HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Endpoint to create a user, the userData is validated
     * @inputs userData: must be fully valid userData to be saved in the db
     * @return Bad request or it returns the saved userData */ 
    @PostMapping(value="/users", consumes="application/json", produces="application/json")
    public ResponseEntity<UserData> createUser(@RequestBody UserData userData) {
        if (!isUserDataValid(userData)) {
            return new ResponseEntity<UserData>(HttpStatus.BAD_REQUEST);
        }
        userService.save(userData);
        return new ResponseEntity<UserData>(userData, HttpStatus.OK);
    }
    
    /**
     * Endpoint to update a user, the userData is validated
     * @inputs userId(from path): id of user to update, userData: must be fully valid userData to be saved in the db
     * @return updated userData or bad request if (not valid or user doesn't exist yet)*/ 
    @PostMapping(value="/users/{id}", consumes="application/json", produces="application/json")
    public ResponseEntity<UserData> updateUser(@PathVariable("id") String userId, @RequestBody UserData userData) {
        try {
            Long id = Long.parseLong(userId);
            UserData savedUserData = userService.findById(id).get(); 
            if(savedUserData != null && isUserDataValid(userData)) {
                savedUserData.setData(
                    userData.getEmail(), 
                    userData.getFirstName(), 
                    userData.getLastName(), 
                    userData.getDob(), 
                    userData.getPassword(), 
                    userData.getCountry()
                );
                userService.save(savedUserData);
                return new ResponseEntity<UserData>(savedUserData, HttpStatus.OK);
            }
        } catch (Exception e) {} // Catches NumberFormatException from parsing and NoSuchElementException from getting savedUserData
        
        return new ResponseEntity<UserData>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint to delete a user
     * @inputs userId(from path): id of user to delete
     * @return Success or failure message */ 
    @DeleteMapping(value="/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId) {
        // TODO: Add case for if the delete fails
        try {
            Long id = Long.parseLong(userId);
            userService.deleteById(id);
            return new ResponseEntity<String>("User Successfully deleted", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<String>("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Validates all fields of userData
     * @inputs userData
     * @return Boolean, true if userData is valid */ 
    private boolean isUserDataValid(UserData userData) {
        // TODO: Improve validation & add unique error messages for each field
        return EMAIL_REGEX.matcher(userData.getEmail()).find() 
            && userData.getFirstName().length() > 0
            && userData.getLastName().length() > 0
            && userData.getDob().before(new Date())
            && userData.getPassword().length() > 0
            && userData.getCountry().length() > 0;
    }
}
