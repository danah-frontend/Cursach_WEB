package ru.rutmiit.services;

import ru.rutmiit.dto.AddUserDto;
import ru.rutmiit.dto.ShowUserInfoDto;
import ru.rutmiit.dto.ShowDetailedUserInfoDto;
import java.util.List;

public interface UserService {
    List<ShowUserInfoDto> getAllUsers();
    ShowDetailedUserInfoDto getUserById(Long id);
    ShowUserInfoDto addUser(AddUserDto addUserDto);
    ShowUserInfoDto updateUser(Long id, AddUserDto addUserDto);
    void deleteUser(Long id);
    ShowUserInfoDto getUserByEmail(String email);
}
