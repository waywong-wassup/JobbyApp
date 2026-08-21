package com.jobapplicationapp.jobby.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test for AppDatabase
 *
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var appDatabase: AppDatabase
    private lateinit var jobApplicationDao: JobApplicationDao
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        jobApplicationDao = appDatabase.jobApplicationDao()
        userDao = appDatabase.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if(::appDatabase.isInitialized)
            {
                appDatabase.close()
            }
    }

    //Test data
    private var user1 = User(1,"Harry", "Potter")
    private var user2 = User(2,"Ron", "Weasley")
    private var jobApplication1 = JobApplication(1,"Janitor", "Mom's basement", 5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired")
    private var jobApplication2 = JobApplication(2,"Librarian","Library", 5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired")

    @Test
    fun testInsertAndRetrieveOneJobApplication() = runBlocking {
        jobApplicationDao.addJobApplication(jobApplication1)
        val addedJobApplication = jobApplicationDao.getAllJobApplications().first()
        assertEquals(addedJobApplication[0], jobApplication1)
    }

    @Test
    fun testInsertAndRetrieveMultipleJobApplication() = runBlocking {
        jobApplicationDao.addJobApplication(jobApplication1)
        jobApplicationDao.addJobApplication(jobApplication2)
        val addedJobApplication = jobApplicationDao.getAllJobApplications().first()
        assertEquals(addedJobApplication[0], jobApplication1)
        assertEquals(addedJobApplication[1], jobApplication2)
    }

    @Test
    fun testInsertAndRetrieveMultipleJobApplicationAndDeleteOne() = runBlocking {
        jobApplicationDao.addJobApplication(jobApplication1)
        jobApplicationDao.addJobApplication(jobApplication2)
        val addedJobApplication = jobApplicationDao.getAllJobApplications().first()
        assertEquals(addedJobApplication[0], jobApplication1)
        assertEquals(addedJobApplication[1], jobApplication2)

        jobApplicationDao.deleteJobApplication(jobApplication1)
        val deletedJobApplication = jobApplicationDao.getAllJobApplications().first()
        //verify only one job application left in the database
        assertEquals(deletedJobApplication[0], jobApplication2)
    }

    @Test
    fun testInsertAndRetrieveOneUser() = runBlocking {
        userDao.insertUser(user1)
        val addedUser = userDao.getAllUsers().first()
        assertEquals(addedUser[0], user1)
    }

    @Test
    fun testInsertAndRetrieveMultipleUsers() = runBlocking {
        userDao.insertUser(user1)
        userDao.insertUser(user2)
        val addedUser = userDao.getAllUsers().first()
        assertEquals(addedUser[0], user1)
        assertEquals(addedUser[1], user2)
    }

    @Test
    fun testInsertAndRetrieveMultipleUsersAndDeleteOne() = runBlocking {
        userDao.insertUser(user1)
        userDao.insertUser(user2)
        val addedUser = userDao.getAllUsers().first()
        assertEquals(addedUser[0], user1)
        assertEquals(addedUser[1], user2)

        userDao.deleteUser(user1)
        val deletedUser = userDao.getAllUsers().first()
        //verify only one user left in the database
        assertEquals(deletedUser[0], user2)
    }

}