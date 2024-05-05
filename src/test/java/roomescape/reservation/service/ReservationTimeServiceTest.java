package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.ReservationFixture.getNextDayReservation;
import static roomescape.fixture.ReservationTimeFixture.get1PM;
import static roomescape.fixture.ReservationTimeFixture.get2PM;
import static roomescape.fixture.ReservationTimeFixture.getNoon;
import static roomescape.fixture.ThemeFixture.getTheme1;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.reservation.controller.dto.AvailableTimeResponse;
import roomescape.reservation.controller.dto.ReservationTimeRequest;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.dao.FakeReservationDao;
import roomescape.reservation.dao.FakeReservationTimeDao;
import roomescape.reservation.dao.FakeThemeDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;

@DisplayName("예약 시간 로직 테스트")
class ReservationTimeServiceTest {
    ReservationRepository reservationRepository;
    ReservationTimeRepository reservationTimeRepository;
    ThemeRepository themeRepository;
    ReservationTimeService reservationTimeService;

    @BeforeEach
    void setUp() {
        reservationRepository = new FakeReservationDao();
        reservationTimeRepository = new FakeReservationTimeDao(reservationRepository);
        themeRepository = new FakeThemeDao(reservationRepository);
        reservationTimeService = new ReservationTimeService(reservationRepository, reservationTimeRepository);
    }

    @DisplayName("예약 시간 생성에 성공한다.")
    @Test
    void create() {
        //given
        String localTime = "12:00";
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(localTime);

        //when
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);

        //then
        assertThat(reservationTimeResponse.startAt()).isEqualTo(localTime);
        assertThat(reservationTimeRepository.findAll()).hasSize(1);
    }

    @DisplayName("예약 시간 조회에 성공한다.")
    @Test
    void findAll() {
        //given
        long id = 1L;
        LocalTime localTime = LocalTime.MIDNIGHT;
        reservationTimeRepository.save(new ReservationTime(id, localTime));

        //when
        List<ReservationTimeResponse> reservationTimes = reservationTimeService.findAll();

        //then
        assertThat(reservationTimes).hasSize(1);
    }

    @DisplayName("예약 시간 삭제에 성공한다.")
    @Test
    void delete() {
        //given
        long id = 1L;
        LocalTime localTime = LocalTime.MIDNIGHT;
        reservationTimeRepository.save(new ReservationTime(id, localTime));

        //when
        reservationTimeService.delete(id);

        //then
        assertThat(reservationTimeRepository.findAll()).hasSize(0);
    }

    @DisplayName("예약이 존재하는 예약 시간을 삭제할 경우 예와가 발생한다.")
    @Test
    void deleteTimeWithReservation() {
        //given
        long id = 1L;
        LocalTime localTime = LocalTime.MIDNIGHT;
        ReservationTime saveTime = reservationTimeRepository.save(new ReservationTime(id, localTime));

        Reservation reservation = new Reservation(1L, LocalDate.now().plusYears(1), saveTime,
                new Theme("name", "description", "thumbnail"));
        reservationRepository.save(reservation);

        //when & then
        assertThatThrownBy(() -> reservationTimeService.delete(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 시간이 증복일 경우, 예외가 발생한다.")
    @Test
    void duplicatedTime() {
        //given
        String localTime = "12:00";
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(localTime);
        reservationTimeRepository.save(new ReservationTime(LocalTime.parse(localTime)));

        //when & then
        assertThatThrownBy(() -> reservationTimeService.create(reservationTimeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 가능한 시간 조회에 성공한다.")
    @Test
    void findAvailableTime() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        reservationTimeRepository.save(get1PM());
        reservationTimeRepository.save(get2PM());
        Theme theme = themeRepository.save(getTheme1());
        Reservation reservation = reservationRepository.save(getNextDayReservation(time, theme));
        reservationRepository.saveReservationList(1L, reservation.getId());

        //when
        List<AvailableTimeResponse> availableTimes
                = reservationTimeService.findAvailableTimes(reservation.getDate(), theme.getId());

        //then
        long count = availableTimes.stream()
                .filter(availableTimeResponse -> !availableTimeResponse.alreadyBooked()).count();
        long expectedCount = reservationTimeService.findAll().size() -
                reservationTimeRepository.findReservedTime(reservation.getDate(), theme.getId()).size();

        assertThat(count)
                .isEqualTo(expectedCount);
    }
}
