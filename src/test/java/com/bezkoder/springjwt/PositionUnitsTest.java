package com.bezkoder.springjwt;

import com.bezkoder.springjwt.models.Company.Company;
import com.bezkoder.springjwt.models.Position.*;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Position.PositionCreateDto;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.Exceptions.PositionCreateException;
import com.bezkoder.springjwt.security.services.PositionsService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PositionUnitsTest {
    private PositionsRepos positions;
    private IUnitRepos units;
    private PositionsService service;
    private PositionCreateDto request;

    @BeforeEach
    void setup() throws Exception {
        positions = mock(PositionsRepos.class);
        units = mock(IUnitRepos.class);
        CategoriesRepos categories = mock(CategoriesRepos.class);
        UserDetailsServiceImpl users = mock(UserDetailsServiceImpl.class);
        Company company = new Company();
        company.setId(1L);
        Category category = new Category();
        category.setId(1L);
        category.setCompany(company);
        User user = new User();
        user.setCompany(company);
        when(users.getCurrentUser()).thenReturn(user);
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        when(positions.save(any())).thenAnswer(call -> {
            Position position = call.getArgument(0);
            position.setId(1L);
            when(positions.findById(1L)).thenReturn(Optional.of(position));
            return position;
        });
        Units liters = new Units();
        liters.setUnitName("л");
        when(units.findByUnitName("л")).thenReturn(liters);
        service = new PositionsService(positions, categories, null, null, null, units, null,
                null, users, "", "", "");
        request = new ObjectMapper().readValue("""
                {"name":"Drink","weight":0.5,"unit":"л","price":100,"categoryId":1,"ingredients":[]}
                """, PositionCreateDto.class);
    }

    @Test
    void savesDecimalOutputAndReturnsUnitsInBothResponses() {
        Position saved = service.addPosition(request, null);
        assertEquals(0.5, saved.getWeight());
        assertEquals("л", saved.toResponseDto().getUnit());
        assertEquals("л", saved.toMinDto().getUnit());
        assertEquals("0.5 л", saved.getFormattedWeight());
    }

    @Test
    void legacyRequestsDefaultToGramsAndDoNotOverwriteUnitsOnEdit() {
        request.setUnit(null);
        Position saved = service.addPosition(request, null);
        assertEquals("г", saved.getUnit());
        saved.setUnit("шт");
        request.setId(1L);
        assertEquals("шт", service.addPosition(request, null).getUnit());
    }

    @Test
    void changesTheUnitOnAnExistingPosition() {
        service.addPosition(request, null);
        Units pieces = new Units();
        pieces.setUnitName("шт");
        when(units.findByUnitName("шт")).thenReturn(pieces);
        request.setId(1L);
        request.setUnit("шт");
        assertEquals("шт", service.addPosition(request, null).toResponseDto().getUnit());
    }

    @Test
    void rejectsUnknownUnitsBeforeSaving() {
        request.setUnit("invalid");
        assertThrows(PositionCreateException.class, () -> service.addPosition(request, null));
        verify(positions, never()).save(any());
    }
}
