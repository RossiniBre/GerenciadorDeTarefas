package infrastructure.persistence;

import domain.model.User;
import domain.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private List<User> userList = new ArrayList<>();

    @Override
    public User save(User user) {
        userList.removeIf(t -> t.getId().equals(user.getId()));
        userList.add(user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : userList){
            if (user.getUsername().equals(username)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteAccount(String id){
        userList.removeIf(user -> user.getId().equals(id));
    }
}