package com.trip.ServiceImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripDeleteResponseDto;
import com.trip.DTO.TripListItemDto;
import com.trip.DTO.TripRequestActionResponseDto;
import com.trip.DTO.TripRequestDto;
import com.trip.DTO.TripUpdateRequest;
import com.trip.Entity.Trip;
import com.trip.Entity.TripMember;
import com.trip.Entity.TripRequest;
import com.trip.Entity.TripRequest.RequestStatus;
import com.trip.Enum.TripStatus;
import com.trip.Exceptions.TripNotFoundException;
import com.trip.Repositories.TripRequestRepository;
import com.trip.Repositories.TripRespository;
import com.trip.Services.TripDomainEventPublisher;
import com.trip.Services.TripNotificationProducer;
import com.trip.Services.TripServices;

@Service
public class TripServiceImpl implements TripServices {

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);
    private static final String TRIP_NOT_FOUND = "Trip not found with id: ";
    private static final String TRIP_DELETED_SUCCESS = "trip deleted successfully.";
    private static final String CACHE_TRIP_BY_ID = "tripById";
    private static final String CACHE_TRIP_PAGES = "tripPages";
    private static final String CACHE_TRIP_USER_PAGES = "tripUserPages";
    private static final String CACHE_TRIP_CATEGORY_PAGES = "tripCategoryPages";

    private final TripRespository tripRespository;
    private final TripRequestRepository tripRequestRepository;
    private final TripNotificationProducer tripNotificationProducer;
    private final TripDomainEventPublisher tripDomainEventPublisher;

    @Value("${trip.reminder.batch-size:200}")
    private int reminderBatchSize;

    @Value("${trip.pagination.max-page-size:100}")
    private int maxPageSize;

    public TripServiceImpl(
            TripRespository tripRespository,
            TripRequestRepository tripRequestRepository,
            TripNotificationProducer tripNotificationProducer,
            TripDomainEventPublisher tripDomainEventPublisher) {
        this.tripRespository = tripRespository;
        this.tripRequestRepository = tripRequestRepository;
        this.tripNotificationProducer = tripNotificationProducer;
        this.tripDomainEventPublisher = tripDomainEventPublisher;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public Trip createTrip(Trip trip) {
        sanitizeTripCollections(trip);
        trip.setTripCreatedAt(Instant.now());
        trip.setPendingRequestCount(0);
        trip.setTotalRequestCount(0);

        Trip savedTrip = tripRespository.save(trip);
        tripNotificationProducer.sendNewTriptNotification(
                savedTrip.getTripOwnerId(),
                savedTrip.getTripId(),
                savedTrip.getTripName());
                
        tripDomainEventPublisher.publishTripCreated(savedTrip);

        return savedTrip;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public TripDeleteResponseDto deleteTrip(String tripId) {
        Trip existingTrip = getTripOrThrow(tripId);
        tripRespository.delete(existingTrip);
        tripRequestRepository.deleteByTripId(tripId);
        tripDomainEventPublisher.publishTripDeleted(tripId, existingTrip.getTripOwnerId());

        return TripDeleteResponseDto.builder()
                .status(TRIP_DELETED_SUCCESS)
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#trip.tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public Trip updateTrip(String authenticatedUserId, TripUpdateRequest trip) {

       
        logger.debug("Updating trip with ID: {}", trip.getTripId());
        Trip existingTrip = getTripOrThrow(trip.getTripId());


        if (trip.getTripCity() != null) {
            existingTrip.setTripCity(trip.getTripCity());
        }
        if (trip.getTripCountry() != null) {
            existingTrip.setTripCountry(trip.getTripCountry());
        }
        if (trip.getTripState() != null) {
            existingTrip.setTripState(trip.getTripState());
        }
        if (trip.getTripStartDate() != null) {
            existingTrip.setTripStartDate(trip.getTripStartDate());
        }
        if (trip.getTripEndDate() != null) {
            existingTrip.setTripEndDate(trip.getTripEndDate());
        }
        if (trip.getTripDuration() != null) {
            existingTrip.setTripDuration(trip.getTripDuration());
        }
        if (trip.getTripDescription() != null) {
            existingTrip.setTripDescription(trip.getTripDescription());
        }
        if (trip.getTripName() != null) {
            existingTrip.setTripName(trip.getTripName());
        }
        if (trip.getTripCategory() != null) {
            existingTrip.setTripCategory(trip.getTripCategory());
        }
        if (trip.getTripStatus() != null) {
            existingTrip.setTripStatus(trip.getTripStatus());
        }
        if (trip.getTripType() != null) {
            existingTrip.setTripType(trip.getTripType());
        }

        existingTrip.setTripUpdatedAt(Instant.now());
        sanitizeTripCollections(existingTrip);

        Trip savedTrip = tripRespository.save(existingTrip);
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("tripName", savedTrip.getTripName());
        updatedFields.put("tripStatus", savedTrip.getTripStatus() == null ? null : savedTrip.getTripStatus().name());
        updatedFields.put("tripCategory", savedTrip.getTripCategory());
        updatedFields.put("tripUpdatedAt", savedTrip.getTripUpdatedAt() == null ? null : savedTrip.getTripUpdatedAt().toString());
        // tripDomainEventPublisher.publishTripUpdated(savedTrip, updatedFields);

        return savedTrip;
    }

    @Override
    @Cacheable(value = CACHE_TRIP_BY_ID, key = "#tripId")
    public Trip getTripById(String tripId) {
        return getTripOrThrow(tripId);
    }

    @Override
    @Cacheable(value = CACHE_TRIP_PAGES, key = "#page + ':' + #size + ':' + #sortBy + ':' + #direction")
    public PageResponseDto<TripListItemDto> getAllTrips(int page, int size, String sortBy, String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Trip> tripPage = tripRespository.findAll(pageable);
        return toPageResponse(tripPage);
    }

    @Override
    @Cacheable(value = CACHE_TRIP_USER_PAGES, key = "#userId + ':' + #page + ':' + #size + ':' + #sortBy + ':' + #direction")
    public PageResponseDto<TripListItemDto> getTripsByUserId(
            String userId,
            int page,
            int size,
            String sortBy,
            String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Trip> tripPage = tripRespository.findByUserId(userId, pageable);
        return toPageResponse(tripPage);
    }

    @Override
    @Cacheable(value = CACHE_TRIP_CATEGORY_PAGES, key = "#category + ':' + #page + ':' + #size + ':' + #sortBy + ':' + #direction")
    public PageResponseDto<TripListItemDto> getTripsByCategory(
            String category,
            int page,
            int size,
            String sortBy,
            String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Trip> tripPage = tripRespository.findByTripCategory(category, pageable);
        return toPageResponse(tripPage);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public Trip sendTripRequest(String tripId, String requestFrom) {
        Trip trip = getTripOrThrow(tripId);
        if (containsMember(safeSet(trip.getTripMembers()), requestFrom)) {
            return trip;
        }

        TripRequest existing = tripRequestRepository.findByTripIdAndRequesterUserId(tripId, requestFrom).orElse(null);
        if (existing == null) {
            TripRequest request = TripRequest.builder()
                    .tripId(tripId)
                    .ownerUserId(trip.getTripOwnerId())
                    .requesterUserId(requestFrom)
                    .status(RequestStatus.PENDING)
                    .requestedAt(Instant.now())
                    .build();
            tripRequestRepository.save(request);
            trip.setTotalRequestCount(trip.getTotalRequestCount() + 1);
        } else if (existing.getStatus() != RequestStatus.PENDING) {
            existing.setStatus(RequestStatus.PENDING);
            existing.setRequestedAt(Instant.now());
            existing.setActedAt(null);
            existing.setActionByUserId(null);
            tripRequestRepository.save(existing);
        }

        long pendingCount = tripRequestRepository.countByTripIdAndStatus(tripId, RequestStatus.PENDING);
        trip.setPendingRequestCount((int) pendingCount);

        Trip savedTrip = tripRespository.save(trip);
        tripNotificationProducer.sendTripRequestNotification(
                requestFrom,
                trip.getTripOwnerId(),
                tripId,
                savedTrip.getTripName());
        // tripDomainEventPublisher.publishTripRequestCreated(tripId, trip.getTripOwnerId(), requestFrom);

        return savedTrip;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public Trip acceptTripRequest(String tripId, String requestFrom, String notificationId) {
        Trip trip = getTripOrThrow(tripId);
        TripRequest tripRequest = tripRequestRepository.findByTripIdAndRequesterUserId(tripId, requestFrom)
                .orElseThrow(() -> new TripNotFoundException("Trip request not found for tripId: " + tripId + " userId: " + requestFrom));

        tripRequest.setStatus(RequestStatus.ACCEPTED);
        tripRequest.setActedAt(Instant.now());
        tripRequest.setActionByUserId(trip.getTripOwnerId());
        tripRequestRepository.save(tripRequest);

        Set<TripMember> members = safeSet(trip.getTripMembers());
        addMember(members, requestFrom);
        trip.setTripMembers(members);
        trip.setPendingRequestCount((int) tripRequestRepository.countByTripIdAndStatus(tripId, RequestStatus.PENDING));

        Trip savedTrip = tripRespository.save(trip);
        tripNotificationProducer.acceptTripRequestNotification(
                trip.getTripOwnerId(),
                requestFrom,
                tripId,
                savedTrip.getTripName());
        if (notificationId != null && !notificationId.isBlank()) {
           tripDomainEventPublisher.publishNotificationDeleteEvent(trip.getTripOwnerId(), notificationId);
        }
        // tripDomainEventPublisher.publishTripRequestAccepted(tripId, trip.getTripOwnerId(), requestFrom);

        return savedTrip;
    }

    @Override
    public PageResponseDto<TripRequestDto> getPendingTripRequests(
            String tripId,
            int page,
            int size,
            String sortBy,
            String direction) {
        getTripOrThrow(tripId);
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<TripRequest> requestPage = tripRequestRepository.findByTripIdAndStatus(tripId, RequestStatus.PENDING, pageable);
        List<TripRequestDto> items = requestPage.getContent().stream()
                .map(TripRequestDto::from)
                .toList();

        return PageResponseDto.<TripRequestDto>builder()
                .items(items)
                .page(requestPage.getNumber())
                .size(requestPage.getSize())
                .totalElements(requestPage.getTotalElements())
                .totalPages(requestPage.getTotalPages())
                .first(requestPage.isFirst())
                .last(requestPage.isLast())
                .hasNext(requestPage.hasNext())
                .hasPrevious(requestPage.hasPrevious())
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public TripRequestActionResponseDto rejectTripRequest(String tripId, String requesterUserId, String actionByUserId) {
        Trip trip = getTripOrThrow(tripId);
        TripRequest tripRequest = tripRequestRepository.findByTripIdAndRequesterUserId(tripId, requesterUserId)
                .orElseThrow(() -> new TripNotFoundException("Trip request not found for tripId: " + tripId + " userId: " + requesterUserId));

        tripRequest.setStatus(RequestStatus.REJECTED);
        tripRequest.setActedAt(Instant.now());
        tripRequest.setActionByUserId(actionByUserId);
        tripRequestRepository.save(tripRequest);

        int pendingCount = (int) tripRequestRepository.countByTripIdAndStatus(tripId, RequestStatus.PENDING);
        trip.setPendingRequestCount(pendingCount);
        tripRespository.save(trip);

        tripDomainEventPublisher.publishTripRequestRejected(tripId, trip.getTripOwnerId(), requesterUserId);

        return TripRequestActionResponseDto.builder()
                .tripId(tripId)
                .requesterUserId(requesterUserId)
                .status(RequestStatus.REJECTED)
                .pendingRequestCount(pendingCount)
                .message("Trip request rejected successfully")
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public TripRequestActionResponseDto cancelTripRequest(String tripId, String requesterUserId, String actionByUserId) {
        Trip trip = getTripOrThrow(tripId);
        TripRequest tripRequest = tripRequestRepository.findByTripIdAndRequesterUserId(tripId, requesterUserId)
                .orElseThrow(() -> new TripNotFoundException("Trip request not found for tripId: " + tripId + " userId: " + requesterUserId));

        tripRequest.setStatus(RequestStatus.CANCELLED);
        tripRequest.setActedAt(Instant.now());
        tripRequest.setActionByUserId(actionByUserId);
        tripRequestRepository.save(tripRequest);

        int pendingCount = (int) tripRequestRepository.countByTripIdAndStatus(tripId, RequestStatus.PENDING);
        trip.setPendingRequestCount(pendingCount);
        tripRespository.save(trip);

        tripDomainEventPublisher.publishTripRequestCancelled(tripId, trip.getTripOwnerId(), requesterUserId);

        return TripRequestActionResponseDto.builder()
                .tripId(tripId)
                .requesterUserId(requesterUserId)
                .status(RequestStatus.CANCELLED)
                .pendingRequestCount(pendingCount)
                .message("Trip request cancelled successfully")
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })

    
    public Trip addTripMember(String tripId, List<TripMember> members) {

    Trip trip = getTripOrThrow(tripId);

    Set<TripMember> memberSet = safeSet(trip.getTripMembers());

    List<String> addedMemberIds = new ArrayList<>();

    for (TripMember member : members) {

        if (member.getUserId() == null) {
            continue; // skip invalid
        }

        boolean added = memberSet.add(member); // Set prevents duplicates

        if (added) {
            addedMemberIds.add(member.getUserId());
        }
    }

    trip.setTripMembers(memberSet);

    Trip savedTrip = tripRespository.save(trip);

    // 🔥 Event payload
    Map<String, Object> updatedFields = new HashMap<>();
    updatedFields.put("membersAdded", addedMemberIds);
    updatedFields.put("memberCount", memberSet.size());

    tripDomainEventPublisher.publishTripUpdated(savedTrip, updatedFields);

    return savedTrip;
}

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_TRIP_BY_ID, key = "#tripId"),
            @CacheEvict(value = CACHE_TRIP_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_USER_PAGES, allEntries = true),
            @CacheEvict(value = CACHE_TRIP_CATEGORY_PAGES, allEntries = true)
    })
    public Trip removeTripMember(String tripId, String memberId) {
        Trip trip = getTripOrThrow(tripId);

        Set<TripMember> members = safeSet(trip.getTripMembers());
        removeMember(members, memberId);
        trip.setTripMembers(members);

        Trip savedTrip = tripRespository.save(trip);
        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("memberRemoved", memberId);
        updatedFields.put("memberCount", members.size());
        tripDomainEventPublisher.publishTripUpdated(savedTrip, updatedFields);
        return savedTrip;
    }

    @Scheduled(cron = "${trip.reminder.cron:0 0 10 * * ?}", zone = "${trip.reminder.zone:Asia/Kolkata}")
    public void sendTripReminders() {
        logger.info("Running trip reminder scheduler at {}", Instant.now());
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        processStartReminderTrips(tomorrow);
        processEndReminderTrips(today);
    }

    private void processStartReminderTrips(LocalDate startDate) {
        int page = 0;
        Page<Trip> trips;

        do {
            Pageable pageable = PageRequest.of(page, reminderBatchSize, Sort.by(Sort.Direction.ASC, "tripId"));
            trips = tripRespository.findByTripStartDate(startDate, pageable);

            for (Trip trip : trips.getContent()) {
                for (TripMember member : safeSet(trip.getTripMembers())) {
                    if (member.getUserId() != null) {
                        tripNotificationProducer.sendTripStartReminderNotification(
                                member.getUserId(),
                                trip.getTripId(),
                                trip.getTripName());
                    }
                }
            }
            page++;
        } while (trips.hasNext());
    }

    private void processEndReminderTrips(LocalDate endDate) {
        int page = 0;
        Page<Trip> trips;

        do {
            Pageable pageable = PageRequest.of(page, reminderBatchSize, Sort.by(Sort.Direction.ASC, "tripId"));
            trips = tripRespository.findByTripEndDate(endDate, pageable);

            for (Trip trip : trips.getContent()) {
                if (trip.getTripStatus() != TripStatus.COMPLETED) {
                    for (TripMember member : safeSet(trip.getTripMembers())) {
                        if (member.getUserId() != null) {
                            tripNotificationProducer.sendTripEndReminderNotification(
                                    member.getUserId(),
                                    trip.getTripId(),
                                    trip.getTripName());
                        }
                    }
                }
            }
            page++;
        } while (trips.hasNext());
    }

    private Trip getTripOrThrow(String tripId) {
        return tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(TRIP_NOT_FOUND + tripId));
    }

    private Set<TripMember> safeSet(Set<TripMember> values) {
        return values == null ? new LinkedHashSet<>() : values;
    }

    private Set<String> safeStringSet(Set<String> values) {
        return values == null ? new LinkedHashSet<>() : values;
    }

    private void sanitizeTripCollections(Trip trip) {
        trip.setTripMembers(safeSet(trip.getTripMembers()));
        trip.setTripTags(safeStringSet(trip.getTripTags()));

        if (trip.getTripHighlights() == null) {
            trip.setTripHighlights(new ArrayList<>());
        }
        if (trip.getTripImages() == null) {
            trip.setTripImages(new ArrayList<>());
        }
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int pageNumber = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), maxPageSize);

        String safeSortBy = StringUtils.hasText(sortBy) ? sortBy : "tripCreatedAt";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, safeSortBy));
    }

    private PageResponseDto<TripListItemDto> toPageResponse(Page<Trip> tripPage) {
        List<TripListItemDto> items = tripPage.getContent().stream()
                .map(TripListItemDto::from)
                .toList();

        return PageResponseDto.<TripListItemDto>builder()
                .items(items)
                .page(tripPage.getNumber())
                .size(tripPage.getSize())
                .totalElements(tripPage.getTotalElements())
                .totalPages(tripPage.getTotalPages())
                .first(tripPage.isFirst())
                .last(tripPage.isLast())
                .hasNext(tripPage.hasNext())
                .hasPrevious(tripPage.hasPrevious())
                .build();
    }

    private boolean containsMember(Set<TripMember> members, String userId) {
        if (userId == null || members == null) {
            return false;
        }
        return members.stream().anyMatch(member -> userId.equals(member.getUserId()));
    }

    private void addMember(Set<TripMember> members, String userId) {
        if (userId == null || members == null) {
            return;
        }
        if (!containsMember(members, userId)) {
            members.add(TripMember.builder()
                    .userId(userId)
                    .build());
        }
    }

    private void removeMember(Set<TripMember> members, String userId) {
        if (userId == null || members == null) {
            return;
        }
        members.removeIf(member -> userId.equals(member.getUserId()));
    }
    public void removeTrip(String userId) {
        tripRespository.deleteByTripOwnerId(userId);
    }
}
