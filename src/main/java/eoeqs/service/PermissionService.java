//package eoeqs.service;
//
//import eoeqs.model.Role;
//import eoeqs.model.City;
//import eoeqs.model.User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PermissionService {
//
//    public boolean canEditOrDelete(User currentUser, City city) {
//        if (city.getUser().equals(currentUser)) {
//            return true;
//        }
//        return currentUser.getRoles().contains(Role.ADMIN);
//    }
//}