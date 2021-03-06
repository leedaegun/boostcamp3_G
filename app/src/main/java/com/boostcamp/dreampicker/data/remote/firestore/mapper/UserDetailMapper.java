package com.boostcamp.dreampicker.data.remote.firestore.mapper;

import com.boostcamp.dreampicker.data.model.UserDetail;
import com.boostcamp.dreampicker.data.remote.firestore.model.UserDetailRemoteData;

import androidx.annotation.NonNull;

public class UserDetailMapper {

    @NonNull
    public static UserDetail toUserDetail(@NonNull String id, @NonNull UserDetailRemoteData response) {

        return new UserDetail(
                id,
                response.getName(),
                response.getProfileImageUrl(),
                response.getFeedCount()
        );
    }
}
