import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './hooks/useAuth'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Tournaments from './pages/Tournaments'
import CreateTournament from './pages/CreateTournament'
import Results from './pages/Results'
import Withdrawals from './pages/Withdrawals'
import Users from './pages/Users'
import Announcements from './pages/Announcements'
import Layout from './components/Layout'
import { CenteredSpinner } from './components/Spinner'

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <CenteredSpinner />
  if (!user) return <Navigate to="/login" replace />
  return children
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="tournaments" element={<Tournaments />} />
        <Route path="tournaments/new" element={<CreateTournament />} />
        <Route path="results" element={<Results />} />
        <Route path="withdrawals" element={<Withdrawals />} />
        <Route path="users" element={<Users />} />
        <Route path="announcements" element={<Announcements />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
