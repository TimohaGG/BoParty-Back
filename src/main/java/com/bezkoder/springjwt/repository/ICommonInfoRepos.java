package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.CommonMenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommonInfoRepos extends JpaRepository<CommonMenuInfo,Long> {
}
