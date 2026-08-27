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
    private var jobApplication1 = JobApplication(1,"Janitor", "Mom's basement", "Auckland",5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired")
    private var jobApplication2 = JobApplication(2,"Librarian","Library", "",5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired")

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
    fun testInsertAndUpdateJobApplication() = runBlocking {
        jobApplicationDao.addJobApplication(jobApplication1)
        val addedJobApplication = jobApplicationDao.getAllJobApplications().first()
        assertEquals(addedJobApplication[0], jobApplication1)
        val updateDetails = jobApplication1.copy(jobTitle = "Mail Man", companyName = "Hedwig",location = "under staircases")
        jobApplicationDao.updateJobApplication(updateDetails)
        val updatedJobApplication = jobApplicationDao.getAllJobApplications().first()
        assertEquals(updatedJobApplication[0], updateDetails)

    }

    @Test
    fun testInsertAndRetrieveOneUser() = runBlocking {
        userDao.insertUser(user1)
        val addedUser = userDao.getCurrentUsers(user1.userId)
        assertEquals(addedUser.first(), user1)
    }

    @Test
    fun testInsertAndUpdateUserName() = runBlocking {
        userDao.insertUser(user1)
        val addedUser = userDao.getCurrentUsers(user1.userId)
        assertEquals(addedUser.first(), user1)
        val updateName = user1.copy(firstName = "Sirius", lastName = "Black")
        userDao.updateUser(updateName)
        val updatedUser = userDao.getCurrentUsers(user1.userId)
        assertEquals(updatedUser.first()!!.firstName, "Sirius")
        assertEquals(updatedUser.first()!!.lastName, "Black")
    }

}