package com.boostcamp.dreampicker.data.remote.firestore;

import android.util.Log;

import com.boostcamp.dreampicker.data.model.MyFeed;
import com.boostcamp.dreampicker.data.model.UserDetail;
import com.boostcamp.dreampicker.data.remote.firestore.mapper.UserDetailMapper;
import com.boostcamp.dreampicker.data.remote.firestore.model.UserDetailRemoteData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class UserRemoteDataSource implements UserDataSource {
    @SuppressWarnings("SpellCheckingInspection")
    @NonNull
    private final FirebaseFirestore firestore;
    private final String COLLECTION_USER = "user";
    private final String COLLECTION_FEED = "feed";
    private final String SUB_COLLECTION_MY_FEEDS = "myFeeds";
    private final String FIELD_DATE = "date";
    private final String FIELD_ENDED = "ended";

    @SuppressWarnings("SpellCheckingInspection")
    @Inject
    UserRemoteDataSource(@NonNull final FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public Single<UserDetail> getUserDetail(@NonNull final String userId) {

        Single<UserDetail> single = Single.create(emitter ->
                firestore.collection(COLLECTION_USER).document(userId)
                        .get()
                        .addOnSuccessListener(document -> {
                            final UserDetailRemoteData response = document.exists()
                                    ? document.toObject(UserDetailRemoteData.class) : null;
                            if (response != null) {
                                emitter.onSuccess(UserDetailMapper
                                        .toUserDetail(document.getId(), response));
                            } else {
                                emitter.onError(new IllegalArgumentException());
                            }
                        })
                        .addOnFailureListener(emitter::onError));

        return single.subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Single<List<MyFeed>> getFeedListByUserId(@NonNull final String userId,
                                                    @NonNull final Date startAfter,
                                                    final int pageSize) {

        Single<List<MyFeed>> single = Single.create(emitter ->
                firestore.collection(COLLECTION_USER).document(userId)
                        .collection(SUB_COLLECTION_MY_FEEDS)
                        .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                        .startAfter(startAfter)
                        .limit(pageSize)
                        .get()
                        .addOnSuccessListener(documents -> {
                            List<MyFeed> feedList = new ArrayList<>();
                            for (DocumentSnapshot document : documents.getDocuments()) {
                                final MyFeed feed = document.toObject(MyFeed.class);
                                if (feed != null) {
                                    feed.setId(document.getId());
                                    feedList.add(feed);
                                }
                            }
                            emitter.onSuccess(feedList);
                        })
                        .addOnFailureListener(emitter::onError));

        return single.subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Completable toggleVoteEnded(@NonNull final String userId,
                                       @NonNull final String feedId,
                                       final boolean isEnded) {

        Completable completable = Completable.create(emitter -> {

            WriteBatch batch = firestore.batch(); // 일괄 수정

            // feed Document
            DocumentReference feedDocRef =
                    firestore.collection(COLLECTION_FEED)
                            .document(feedId);

            // myFeeds Document
            DocumentReference myFeedDocRef =
                    firestore.collection(COLLECTION_USER)
                            .document(userId)
                            .collection(SUB_COLLECTION_MY_FEEDS)
                            .document(feedId);

            batch.update(feedDocRef, FIELD_ENDED, isEnded);
            batch.update(myFeedDocRef, FIELD_ENDED, isEnded);

            batch.commit()
                    .addOnSuccessListener(__ -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });

        return completable.subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Completable insertNewUser(@NonNull final String userId,
                                     @NonNull final UserDetailRemoteData userDetail) {

        Completable completable = Completable.create(emitter -> {
            final DocumentReference reference =
                    firestore.collection(COLLECTION_USER).document(userId);

            firestore.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(reference);
                if (!snapshot.exists()) {
                    transaction.set(reference, userDetail);
                } else {
                    transaction.update(reference,
                            "profileImageUrl",
                            userDetail.getProfileImageUrl());
                }
                return true;
            })
                    .addOnSuccessListener(__ -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });

        return completable.subscribeOn(Schedulers.io());
    }
}
