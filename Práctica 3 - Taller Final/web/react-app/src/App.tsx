import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Landing from './pages/Landing';
import Home from './pages/Home';
import MovieDetail from './pages/MovieDetail';
import Login from './pages/Login';
import Register from './pages/Register';
import MyMovies from './pages/MyMovies';
import AdminPanel from './pages/AdminPanel';
import Friends from './pages/Friends';
import BlockchainExplorer from './pages/BlockchainExplorer';

const App: React.FC = () => {
  return (
    <Routes>
      <Route 
        path="/" 
        element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/welcome" 
        element={
          <ProtectedRoute requireAuth={false}>
            <Landing />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/login" 
        element={
          <ProtectedRoute requireAuth={false}>
            <Login />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/register" 
        element={
          <ProtectedRoute requireAuth={false}>
            <Register />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/movie/:id" 
        element={
          <ProtectedRoute>
            <MovieDetail />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/my-movies" 
        element={
          <ProtectedRoute>
            <MyMovies />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/friends" 
        element={
          <ProtectedRoute>
            <Friends />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/blockchain" 
        element={
          <ProtectedRoute>
            <BlockchainExplorer />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin"
        element={
          <ProtectedRoute adminOnly>
            <AdminPanel />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<h1 style={{ color: 'white' }}>404 - Página no encontrada</h1>} />
    </Routes>
  );
};

export default App;